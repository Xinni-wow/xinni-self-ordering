<template>
  <view>
    <!-- 悬浮按钮 -->
    <view
        class="float-button"
        :style="{ right: right + 'px', bottom: bottom + 'px' }"
        @touchstart="handleTouchStart"
        @touchmove.stop.prevent="handleTouchMove"
        @touchend="handleTouchEnd"
        @click="navigateToRecommendation"
    >
      <image
          class="float-image"
          src="/static/images/robot.png"
          mode="aspectFill"
      ></image>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// 定义位置状态
const right = ref(20)
const bottom = ref(100)

// 触摸起始位置
let startX = 0
let startY = 0
// 按钮初始位置
let startRight = 0
let startBottom = 0

// 处理触摸开始
const handleTouchStart = (e: TouchEvent) => {
  // 记录触摸起始点
  startX = e.touches[0].clientX
  startY = e.touches[0].clientY
  // 记录按钮当前位置
  startRight = right.value
  startBottom = bottom.value
}

// 处理触摸移动
const handleTouchMove = (e: TouchEvent) => {
  // 计算移动距离
  const moveX = e.touches[0].clientX - startX
  const moveY = e.touches[0].clientY - startY

  // 计算新位置
  let newRight = startRight - moveX
  let newBottom = startBottom + moveY

  // 获取屏幕宽高
  const sysInfo = uni.getSystemInfoSync()
  const windowWidth = sysInfo.windowWidth
  const windowHeight = sysInfo.windowHeight

  // 限制拖动范围
  if (newRight < 0) newRight = 0
  if (newRight > windowWidth - 60) newRight = windowWidth - 60
  if (newBottom < 0) newBottom = 0
  if (newBottom > windowHeight - 60) newBottom = windowHeight - 60

  // 更新位置
  right.value = newRight
  bottom.value = newBottom
}

// 处理触摸结束
const handleTouchEnd = () => {
  // 可以在这里添加吸附到边缘的逻辑
  const sysInfo = uni.getSystemInfoSync()
  const windowWidth = sysInfo.windowWidth

  // 如果靠近边缘，则自动吸附到边缘
  if (right.value < windowWidth / 2 - 30) {
    right.value = 10
  } else {
    right.value = windowWidth - 70
  }
}

// 跳转到推荐页面
const navigateToRecommendation = () => {
  uni.navigateTo({
    url: '/pages/recommendation/recommendation'
  })
}
</script>

<style scoped>
.float-button {
  position: fixed;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #007aff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.float-image {
  width: 30px;
  height: 30px;
}
</style>
