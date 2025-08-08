<template>
  <view class="order_content">
    <scroll-view class="order_content_box" scroll-y scroll-top="0rpx">
      <!-- 用餐方式选择 -->
      <view class="dine_type">
        <view class="title"></view>
        <view class="options">
          <view
            class="option"
            :class="{ active: dineType === 1 }"
            @click="selectDineType(1)"
          >
            <image
              class="icon"
              :src="dineType === 1 ? '../../static/icon/table_active.png' : '../../static/icon/table.png'"
            ></image>
            <text style="font-size: large;">堂食</text>
          </view>
          <view
            class="option"
            :class="{ active: dineType === 2 }"
            @click="selectDineType(2)"
          >
            <image
              class="icon"
              :src="dineType === 2 ? '../../static/icon/package_active.png' : '../../static/icon/package.png'"
            ></image>
            <text style="font-size: large;">打包</text>
          </view>
        </view>
      </view>


      <!-- 两个白框栏 -->
      <view class="order_list_cont">
        <!-- 1、订单菜品列表 -->
        <view class="order_list">
          <view class="word_text">
            <text class="word_style">订单明细</text>
          </view>
          <view class="order-type">
            <view class="type_item" v-for="(obj, index) in cartList" :key="index">
              <view class="dish_img">
                <image mode="aspectFill" :src="obj.pic" class="dish_img_url"></image>
              </view>
              <view class="dish_info">
                <view class="dish_name"> {{ obj.name }} </view>
                <view v-if="obj.dishFlavor" class="dish_flavor"> {{ obj.dishFlavor }} </view>
                <view class="dish_amount">
                  <text v-if="obj.number && obj.number > 0" class="dish_number">x {{ obj.number }}</text>
                </view>
                <view class="dish_price"> <text class="ico">￥</text> {{ obj.amount }} </view>
              </view>
            </view>
            <view class="word_text" v-if="dineType === 2">
              <view class="word_left">打包费</view>
              <view class="word_right">￥{{ CartAllNumber }}</view>
            </view>
            <view class="all_price">
              <text class="word_right">总价 ￥{{ CartAllPrice }}</text>
            </view>
          </view>
        </view>
        <!-- 2、备注+餐具份数 -->
        <view class="order_list">
          <view class="bottom_text" @click="goRemark">
            <view class="text_left">备注</view>
            <view class="text_right">{{ remark || '选择口味等' }}</view>
            <view class="right_image">
              <image class="to_right" src="../../static/icon/toRight.png"></image>
            </view>
          </view>
          <view class="bottom_text" @click="chooseCooker">
            <view class="text_left">餐具份数</view>
            <view class="text_right">{{ getCookerInfo() }}</view>
            <view class="right_image">
              <image class="to_right" src="../../static/icon/toRight.png"></image>
            </view>
          </view>
        </view>
      </view>
      <view class="blank"></view>
    </scroll-view>
    <!-- 底部购物车 -->
    <view class="footer_order_buttom order_form">
      <view class="order_number">
        <image src="../../static/images/cart_active.png" class="order_number_icon"></image>
        <view class="order_dish_num"> {{ CartAllNumber }} </view>
      </view>
      <view class="order_price">
        <text class="ico">￥ </text> {{ parseFloat((Math.round(CartAllPrice * 100) / 100).toFixed(2)) }}</view
      >
      <view class="order_but">
        <view class="order_but_rit" @click="payOrderHandle()"> 去支付 </view>
      </view>
    </view>
    <view class="mask-box"></view>

    <!-- 选择餐具遮罩层 -->
    <view class="pop_mask" v-show="openCooker" @click="openCooker = !openCooker">
      <view class="cook_pop" @click.stop="openCooker = openCooker">
        <view class="top_title">
          <view class="title"> 选择餐具份数 </view>
          <view class="tips"> 应监管条例要求，商家不能主动提供一次性餐具 </view>
          <view class="close" @click="closeMask">
            <image src="../../static/icon/close.png" class="close_img" />
          </view>
        </view>
        <picker-view class="picker" indicator-style="height: 50px;" :value="cookers" @change="pickerChange">
          <picker-view-column>
            <view v-for="item in cookers" :key="item" style="line-height: 50px; text-align: center">
              {{ item === -1 ? '无需餐具' : item === 0 ? '商家依据餐量提供' : item === 11 ? '10份以上' : item + '份' }}
            </view>
          </picker-view-column>
        </picker-view>
        <view class="comfirm">
          <view class="after_action">
            <label class="checkbox">
              <radio class="radio" color="#00aaff" value="cb" :checked="radioStatus" @click="radioChange" />
              {{ cookerNum === -2 || cookerNum === -1 ? '以后都无需餐具' : '以后都需要餐具，商家依据餐量提供' }}
            </label>
            <button class="comfirm_btn" @click="openCooker = !openCooker">确定</button>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import {getCartAPI} from '@/api/cart'
import {submitOrderAPI, getUnPayOrderAPI} from '@/api/order'
import type {CartItem} from '@/types/cart'
import {useAddressStore} from '@/stores/modules/address'
import {onLoad, onShow} from '@dcloudio/uni-app'
import {ref} from 'vue'

// store
const store = useAddressStore()

// 购物车列表
const cartList = ref<CartItem[]>([])
const CartAllNumber = ref(0)
const CartAllPrice = ref(0)

// 用餐方式 1:堂食 2:打包
const dineType = ref(1)

const platform = ref('ios')

const openCooker = ref(false)
const cookerNum = ref(-2)
const cookers = ref([-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11])

const radioStatus = ref(false)

const remark = ref('')
const arrivalTime = ref('')

// 查询获取购物车列表
const getCartList = async () => {
  const res = await getCartAPI()
  console.log('初始化购物车列表', res)
  cartList.value = res.data
  // 计算总数量
  CartAllNumber.value = cartList.value.reduce((acc, cur) => acc + cur.number, 0)
  // 计算总价格 = 菜品总价 + 打包费 (打包费仅在打包时收取)
  CartAllPrice.value = cartList.value.reduce((acc, cur) => acc + cur.amount * cur.number, 0) +
                      (dineType.value === 2 ? CartAllNumber.value : 0)
  console.log('CartAllNumber', CartAllNumber.value)
  console.log('CartAllPrice', CartAllPrice.value)
}

onLoad(async (options: any) => {
  console.log('options', options)
  if (options.remark) {
    remark.value = options.remark
  }
  // 获取购物车列表
  await getCartList()
  // 获取一小时以后的时间，作为预计取餐的时间
  getHarfAnOur()
  // 默认选择的餐具状态
  if (store.defaultCook === '无需餐具') {
    cookerNum.value = -1
  } else if (store.defaultCook === '商家依据餐量提供') {
    cookerNum.value = 0
  }
})

onShow(async (options: any) => {
  console.log('options', options)
  await getCartList()
})

// 初始化平台：ios/android
const initPlatform = () => {
  const res = uni.getSystemInfoSync()
  platform.value = res.platform
}

// 日期转字符串格式
const DateToStr = (date: Date) => {
  var year = date.getFullYear() //年
  var month = date.getMonth() //月
  var day = date.getDate() //日
  var hours = date.getHours() //时
  var min = date.getMinutes() //分
  var second = date.getSeconds() //秒
  return (
    year +
    '-' +
    (month + 1 > 9 ? month + 1 : '0' + (month + 1)) +
    '-' +
    (day > 9 ? day : '0' + day) +
    ' ' +
    (hours > 9 ? hours : '0' + hours) +
    ':' +
    (min > 9 ? min : '0' + min) +
    ':' +
    (second > 9 ? second : '0' + second)
  )
}
// 获取一小时以后的时间
const getHarfAnOur = () => {
  const date = new Date()
  date.setTime(date.getTime() + 3600000)
  const formattedDate = DateToStr(date)
  let hours = date.getHours()
  let minutes = date.getMinutes()
  if (hours < 10) hours = parseInt('0' + hours)
  if (minutes < 10) minutes = parseInt('0' + minutes)
  arrivalTime.value = hours + ':' + minutes
}

// 选择用餐方式
const selectDineType = (type: number) => {
  dineType.value = type
  // 重新计算价格（打包需要加打包费）
  CartAllPrice.value = cartList.value.reduce((acc, cur) => acc + cur.amount * cur.number, 0) +
                      (dineType.value === 2 ? CartAllNumber.value : 0)
}

// 标签文字转数字
const trans = (item: string) => {
  switch (item) {
    case '公司':
      return '1'
    case '家':
      return '2'
    case '学校':
      return '3'
    default:
      return '4'
  }
}

// 去备注页面
const goRemark = () => {
  uni.redirectTo({
    url: '/pages/remark/remark',
  })
}
// 选择餐具
const chooseCooker = () => {
  openCooker.value = true
}
// 餐具对应信息
const getCookerInfo = () => {
  if (cookerNum.value === -2) return '请依据实际情况填写，避免浪费'
  else if (cookerNum.value === -1) return '无需餐具'
  else if (cookerNum.value === 0) return '商家依据餐量提供'
  else if (cookerNum.value === 11) return '10份以上'
  else return cookerNum.value + '份'
}
const pickerChange = (ev: any) => {
  console.log(ev.detail.value)
  cookerNum.value = ev.detail.value[0] - 1
}
// 改变radio状态，顺便改变store里默认餐具选择的状态
const radioChange = () => {
  radioStatus.value = !radioStatus.value
  if (radioStatus.value) {
    store.defaultCook = cookerNum.value === -1 ? '无需餐具' : '商家依据餐量提供'
  } else {
    store.defaultCook = '请依据实际情况填写，避免浪费'
  }
}
const closeMask = () => {
  openCooker.value = false
}

// 支付下单
const payOrderHandle = async () => {
  // 先去后端查询一下是否有未支付但没取消的订单，如果有的话无法下单
  const unPayRes = await getUnPayOrderAPI()
  console.log('未支付订单', unPayRes)
  if (unPayRes.data !== 0) {
    console.log('有未支付订单', unPayRes.data)
    uni.showToast({
      title: '有未支付订单，请先支付或取消！',
      icon: 'none',
    })
    return false
  }
  // 餐具： -2未选择，-1无需餐具，0商家依据餐量提供，其他数字具体数量
  if (cookerNum.value === -2) {
    uni.showToast({
      title: '请选择餐具份数',
      icon: 'none',
    })
    return false
  }

  const params = {
    payMethod: 1, // 默认微信支付
    remark: remark.value,
    pickupStatus: 1, // 默认立即制作
    tablewareNumber: cookerNum.value, // 餐具份数
    tablewareStatus: cookerNum.value === 0 ? 1 : 0, // 餐具状态: 1按餐量提供，0选择具体数量
    packAmount: dineType.value === 2 ? CartAllNumber.value : 0, // 打包费仅在打包时收取
    amount: CartAllPrice.value,
    dineType: dineType.value // 用餐方式 1:堂食 2:打包
  }
  console.log('生成订单params', params)
  const res = await submitOrderAPI(params)
  if (res.code === 0) {
    console.log('订单生成成功', res.data)
    // 此时订单已生成，跳转到支付页面
    uni.redirectTo({
      url:
        '/pages/pay/pay?' +
        'orderId=' +
        res.data!.id +
        '&orderAmount=' +
        res.data!.orderAmount +
        '&orderNumber=' +
        res.data!.orderNumber +
        '&orderTime=' +
        res.data!.orderTime,
    })
  } else {
    uni.showToast({
      title: res.msg || '操作失败',
      icon: 'none',
    })
  }
}
</script>

<style lang="less" scoped>
.order_content {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20rpx 0 0 0;
  position: relative;
  background-color: #cceeff;
  .order_content_box {
    width: 100%;
    height: 100%;
    // 不知道为啥要加这个，才有底部的padding出现
    .blank {
      height: 1rpx;
    }
  }
  box-sizing: border-box;

  // 用餐方式选择
  .dine_type {
    width: 730rpx;
    height: 200rpx;
    background-color: #fff;
    margin: 0 auto 20rpx;
    border-radius: 12rpx;
    padding: 20rpx;
    box-sizing: border-box;

    .title {
      font-size: 32rpx;
      font-weight: bold;
      margin-bottom: 20rpx;
      color: #333;
    }

    .options {
      display: flex;
      justify-content: space-around;

      .option {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 20rpx;
        border-radius: 10rpx;
        border: 2rpx solid #ddd;

        &.active {
          border-color: #00aaff;
          background-color: #e6f7ff;
        }

        .icon {
          width: 180rpx;
          height: 20rpx;
          margin-bottom: 10rpx;
        }

        text {
          font-size: 28rpx;
        }
      }
    }
  }

  // 预计取餐时间
  .pickup_time {
    width: 730rpx;
    background-color: #fff;
    margin: 0 auto 20rpx;
    border-radius: 12rpx;
  }

  // 订单container，包括订单明细+备注
  .order_list_cont {
    width: 730rpx;
    margin: 0 auto;
    // 订单明细/备注 的白色圆角矩形容器
    .order_list {
      border-radius: 15rpx;
      background-color: #fff;
      width: 100%;
      height: 100%;
      box-sizing: border-box;
      position: relative;
      margin-bottom: 20rpx;
      &:last-child {
        margin-bottom: 176rpx;
      }
      // 菜品列表
      .order-type {
        padding: 40rpx 0 10rpx 0;
        // 菜品列表的每个元素
        .type_item {
          display: flex;
          margin-bottom: 30rpx;
          .dish_img {
            width: 100rpx;
            margin: 0 20rpx 0 32rpx;
            .dish_img_url {
              display: block;
              width: 100rpx;
              height: 100rpx;
              border-radius: 8rpx;
            }
          }
          .dish_info {
            position: relative;
            flex: 1;
            margin-right: 20rpx;
            // margin: 0 20rpx 20rpx 0;
            // margin-bottom: 200rpx;
            .dish_name {
              font-size: 30rpx;
              font-weight: bold;
              color: #20232a;
            }
            .dish_flavor {
              font-size: 24rpx;
              color: #818693;
              height: 30rpx;
              line-height: 30rpx;
              margin-top: 10rpx;
            }
            .dish_amount {
              font-size: 24rpx;
              color: #818693;
              height: 30rpx;
              line-height: 30rpx;
              margin-top: 10rpx;
              .ico {
                font-size: 24rpx;
              }
              .dish_number {
                padding: 10rpx 0;
                font-size: 24rpx;
              }
            }
            .dish_price {
              position: absolute;
              right: 20rpx;
              bottom: 40rpx;
              display: flex;
              font-size: 32rpx;
              color: #e94e3c;
              font-family: DIN, DIN-Medium;
              font-weight: 500;
              .ico {
                line-height: 42rpx;
                font-size: 24rpx;
              }
            }
          }
        }
      }
      .seize_seat {
        width: 100%;
        height: 98rpx;
      }
      .word_text {
        display: flex;
        align-items: center;
        margin: 0 20rpx 0 30rpx;
        border-bottom: 1px solid #efefef;
        height: 120rpx;
        line-height: 120rpx;
        .word_left {
          width: 50%;
          height: 44rpx;
          opacity: 1;
          font-size: 32rpx;
          text-align: left;
          color: #333333;
          line-height: 44rpx;
          letter-spacing: 0px;
        }
        .word_right {
          width: 50%;
          height: 44rpx;
          opacity: 1;
          font-size: 32rpx;
          text-align: right;
          color: #333333;
          line-height: 44rpx;
          letter-spacing: 0px;
          padding-right: 20rpx;
        }
      }
      .all_price {
        margin: 0 16rpx 0 22rpx;
        height: 120rpx;
        line-height: 120rpx;
        .word_right {
          height: 44rpx;
          opacity: 1;
          font-size: 32rpx;
          text-align: left;
          color: #333333;
          line-height: 44rpx;
          letter-spacing: 0px;
          padding-left: 500rpx;
        }
      }
      .bottom_text {
        display: flex;
        align-items: center;
        margin: 0 20rpx 0 30rpx;
        height: 100rpx;
        line-height: 100rpx;
        .text_left {
          width: 30%;
          height: 44rpx;
          opacity: 1;
          font-size: 32rpx;
          text-align: left;
          color: #333333;
          line-height: 44rpx;
          letter-spacing: 0px;
        }
        .text_right {
          width: 70%;
          height: 44rpx;
          font-size: 24rpx;
          text-align: right;
          color: #666666;
          line-height: 44rpx;
          letter-spacing: 0px;
          padding-right: 20rpx;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        .right_image {
          width: 30rpx;
          height: 100%;
          position: relative;
          .to_right {
            width: 30rpx;
            height: 30rpx;
            vertical-align: middle;
            margin-bottom: 10rpx;
            position: absolute;
            top: 50%;
            right: 6rpx;
            transform: translateY(-50%);
          }
        }
      }
    }
  }
  .footer_order_buttom {
    position: fixed;
    display: flex;
    bottom: 48rpx;
    width: calc(100% - 60rpx);
    height: 88rpx;
    margin: 0 auto;
    background: rgba(0, 0, 0, 0.9);
    border-radius: 50rpx;
    box-shadow: 0px 6rpx 10rpx 0px rgba(0, 0, 0, 0.25);
    z-index: 999;
    padding: 0rpx 10rpx;
    box-sizing: border-box;
    .order_number {
      position: relative;
      width: 120rpx;
      .order_number_icon {
        position: absolute;
        display: block;
        width: 120rpx;
        height: 118rpx;
        left: 12rpx;
        bottom: 0px;
      }
      .order_dish_num {
        position: absolute;
        display: inline-block;
        z-index: 9;
        // width: 36rpx;
        min-width: 12rpx;
        height: 36rpx;
        line-height: 36rpx;
        padding: 0 12rpx;
        left: 92rpx;
        font-size: 24rpx;
        top: -8rpx;
        // text-align: center;
        border-radius: 20rpx;
        background-color: #e94e3c;
        color: #fff;
        font-weight: 500;
      }
    }
    .order_price {
      flex: 1;
      text-align: left;
      color: #fff;
      line-height: 88rpx;
      padding-left: 34rpx;
      box-sizing: border-box;
      font-size: 36rpx;
      font-weight: bold;
      .ico {
        font-size: 24rpx;
      }
    }
    .order_but {
      // background-color: #d8d8d8;
      // width: 364rpx;
      height: 72rpx;
      line-height: 72rpx;
      border-radius: 72rpx;
      text-align: center;
      margin-top: 8rpx;
      display: flex;
      .order_but_left {
        flex: 1;
        background-color: #473d26;
        color: #ffb302;
        border-radius: 72rpx 0 0 72rpx;
      }
      .order_but_rit {
        // flex: 1;
        width: 200rpx;
        border-radius: 72rpx;
        background: #22bbff;
        font-size: 30rpx;
        font-family: PingFangSC, PingFangSC-Medium;
        font-weight: 500;
        color: #fff;
      }
    }
  }
  .pop_mask {
    position: fixed;
    width: 100%;
    height: 100vh;
    top: 0;
    left: 0;
    z-index: 999;
    background-color: rgba(0, 0, 0, 0.4);
    .cook_pop {
      width: 100%;
      height: 60vh;
      position: absolute;
      bottom: 0;
      left: 0;
      background-color: #fff;
      border-radius: 20rpx 20rpx 0 0;
      padding: 20rpx 30rpx 30rpx 30rpx;
      box-sizing: border-box;

      .top_title {
        // display: flex;
        // flex-direction: row;
        position: relative;
        // justify-content: space-between;
        border-bottom: solid 1px #ebeef5;
        padding-bottom: 20rpx;

        .title {
          width: 100%;
          text-align: center;
          font-size: 30rpx;
          line-height: 50rpx;
          font-weight: bold;
          color: #20232a;
        }
        .tips {
          width: 100%;
          text-align: center;
          font-size: 20rpx;
          line-height: 40rpx;
          color: #999999;
        }
        .close {
          position: absolute;
          top: 20rpx;
          right: 0;

          .close_img {
            width: 40rpx;
            height: 40rpx;
          }
        }
      }
      .picker {
        width: 100%;
        height: 400rpx;
      }
      .comfirm {
        display: flex;
        justify-content: space-between;
        align-items: center;
        // margin-top: 20rpx;
        width: 600rpx;
        margin: 20rpx auto;
        background-color: #fea;
        border-radius: 10rpx 10rpx 30rpx 30rpx;
        .after_action {
          // height: 200rpx;
          font-size: 24rpx;
          line-height: 60rpx;
          color: #999999;
          .checkbox {
            padding: 10rpx;
            radio .wx-radio-input {
              width: 30rpx;
              height: 30rpx;
              border-radius: 50%;
            }
          }
          .comfirm_btn {
            width: 600rpx;
            height: 80rpx;
            line-height: 80rpx;
            border-radius: 40rpx;
            background: #00aaff;
            color: #fff;
            font-size: 30rpx;
            text-align: center;
            letter-spacing: 0px;
            display: flex;
            align-items: center;
            justify-content: center;
          }
        }
      }
    }
  }
  .mask-box {
    position: absolute;
    height: 176rpx;
    width: 100%;
    bottom: 0;
    background-color: #f6f6f6;
    opacity: 0.5;
  }
}

.dish_detail_pop {
  width: calc(100vw - 160rpx);
  box-sizing: border-box;
  position: relative;
  top: 50%;
  left: 50%;
  padding: 40rpx;
  transform: translateX(-50%) translateY(-50%);
  background: #fff;
  border-radius: 20rpx;

  .div_big_image {
    width: 100%;
    height: 320rpx;
    border-radius: 10rpx;
  }

  .title {
    font-size: 40rpx;
    line-height: 80rpx;
    text-align: center;
    font-weight: bold;
  }

  .dish_items {
    height: 60vh;
  }

  .but_item {
    display: flex;
    position: relative;
    flex: 1;

    .price {
      text-align: left;
      color: #e94e3c;
      line-height: 88rpx;
      box-sizing: border-box;
      font-size: 48rpx;
      font-weight: bold;

      .ico {
        font-size: 28rpx;
      }
    }

    .active {
      position: absolute;
      right: 0rpx;
      bottom: 20rpx;
      display: flex;

      .dish_add,
      .dish_red {
        display: block;
        width: 72rpx;
        height: 72rpx;
      }

      .dish_number {
        padding: 0 10rpx;
        line-height: 72rpx;
        font-size: 30rpx;
        font-family: PingFangSC, PingFangSC-Medium;
        font-weight: 500;
      }

      .dish_card_add {
        width: 200rpx;
        line-height: 60rpx;
        text-align: center;
        font-weight: 500;
        font-size: 28rpx;
        opacity: 1;
        background: #ffc200;
        border-radius: 30rpx;
      }
    }
  }
}

.more_norm_pop {
  width: calc(100vw - 160rpx);
  box-sizing: border-box;
  position: relative;
  top: 50%;
  left: 50%;
  padding: 40rpx;
  transform: translateX(-50%) translateY(-50%);
  background: #fff;
  border-radius: 20rpx;

  .div_big_image {
    width: 100%;
    border-radius: 10rpx;
  }

  .title {
    font-size: 40rpx;
    line-height: 80rpx;
    text-align: center;
    font-weight: bold;
  }

  .items_cont {
    display: flex;
    flex-wrap: wrap;
    margin-left: -14rpx;
    max-height: 50vh;

    .item_row {
      .flavor_name {
        height: 40rpx;
        opacity: 1;
        font-size: 28rpx;
        font-family: PingFangSC, PingFangSC-Regular;
        font-weight: 400;
        text-align: left;
        color: #666666;
        line-height: 40rpx;
        padding-left: 10rpx;
        padding-top: 20rpx;
      }

      .flavor_item {
        display: flex;
        flex-wrap: wrap;

        .item {
          border: 1px solid #ffb302;
          border-radius: 12rpx;
          margin: 20rpx 10rpx;
          padding: 0 26rpx;
          height: 60rpx;
          line-height: 60rpx;
          font-family: PingFangSC, PingFangSC-Regular;
          font-weight: 400;
          color: #333333;
        }

        .act {
          // background: linear-gradient(144deg, #ffda05 18%, #ffb302 80%);
          background: #ffc200;
          border: 1px solid #ffc200;
          font-family: PingFangSC, PingFangSC-Medium;
          font-weight: 500;
        }
      }
    }
  }

  .but_item {
    display: flex;
    position: relative;
    flex: 1;
    padding-left: 10rpx;
    margin: 34rpx 0 -20rpx 0;

    .price {
      text-align: left;
      color: #e94e3c;
      line-height: 88rpx;
      box-sizing: border-box;
      font-size: 48rpx;
      font-family: DIN, DIN-Medium;
      font-weight: 500;

      .ico {
        font-size: 28rpx;
      }
    }

    .active {
      position: absolute;
      right: 0rpx;
      bottom: 20rpx;
      display: flex;

      .dish_add,
      .dish_red {
        display: block;
        width: 72rpx;
        height: 72rpx;
      }

      .dish_number {
        line-height: 72rpx;
        font-size: 24rpx;
        font-family: PingFangSC, PingFangSC-Medium;
        font-weight: 500;
      }

      .dish_card_add {
        width: 200rpx;
        height: 60rpx;
        line-height: 60rpx;
        text-align: center;
        font-weight: 500;
        font-size: 28rpx;
        opacity: 1;
        // background: linear-gradient(144deg, #ffda05 18%, #ffb302 80%);
        background: #ffc200;
        border-radius: 30rpx;
      }
    }
  }
}

.lodding {
  position: relative;
  top: 40%;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  align-items: center;

  .lodding_ico {
    width: 160rpx;
    height: 160rpx;
    border-radius: 100%;
  }
}
</style>
