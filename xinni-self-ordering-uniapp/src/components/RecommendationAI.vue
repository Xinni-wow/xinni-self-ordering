<template>
  <view>
    <!-- 悬浮球按钮 -->
    <view
        class="float-sphere"
        :class="{ dragging: isDragging }"
        :style="{ right: right + 'px', bottom: bottom + 'px' }"
        @touchstart="handleTouchStart"
        @touchmove.stop.prevent="handleTouchMove"
        @touchend="handleTouchEnd"
        @click="navigateToRecommendation"
    >
      <image
          class="sphere-icon"
          src="/static/images/robot.png"
          mode="aspectFit"
      ></image>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// 定义位置状态
const right = ref(30)
const bottom = ref(150)
const isDragging = ref(false)

// 系统信息（只获取一次）
const sysInfo = uni.getSystemInfoSync()
const windowWidth = ref(sysInfo.windowWidth)
const windowHeight = ref(sysInfo.windowHeight)

// 触摸起始位置和初始位置
let startX = 0
let startY = 0
let startRight = 0
let startBottom = 0

// 处理触摸开始
const handleTouchStart = (e: TouchEvent) => {
  isDragging.value = true
  const touch = e.touches[0]

  // 记录触摸起始点和按钮当前位置
  startX = touch.clientX
  startY = touch.clientY
  startRight = right.value
  startBottom = bottom.value
}

// 处理触摸移动 - 使用增量计算
const handleTouchMove = (e: TouchEvent) => {
  if (!isDragging.value) return

  const touch = e.touches[0]

  // 计算移动增量（当前触摸点 - 起始触摸点）
  const deltaX = touch.clientX - startX
  const deltaY = touch.clientY - startY

  // 计算新位置（初始位置 + 增量）
  let newRight = startRight - deltaX  // right是距离右边的距离，所以减
  let newBottom = startBottom - deltaY // bottom是距离底部的距离，所以减

  // 限制拖动范围（保留5px边距）
  newRight = Math.max(5, Math.min(newRight, windowWidth.value - 55)) // 55 = 50px球宽 + 5px边距
  newBottom = Math.max(5, Math.min(newBottom, windowHeight.value - 55))

  // 更新位置
  right.value = newRight
  bottom.value = newBottom
}

// 处理触摸结束 - 添加弹性吸附效果
const handleTouchEnd = () => {
  isDragging.value = false

  // 计算悬浮球中心点X坐标
  const sphereCenterX = right.value + 25 // 25 = 球半径

  // 决定吸附到哪一侧
  if (sphereCenterX < windowWidth.value / 2) {
    right.value = 15 // 吸附到左边
  } else {
    right.value = windowWidth.value - 55 // 吸附到右边
  }

  // 限制底部位置（防止吸附后超出底部）
  bottom.value = Math.max(5, Math.min(bottom.value, windowHeight.value - 55))
}

// 跳转到推荐页面
const navigateToRecommendation = () => {
  if (!isDragging.value) { // 只有不是拖动时才跳转
    uni.navigateTo({
      url: '/pages/recommendation/recommendation'
    })
  }
}
</script>

<style scoped>
.float-sphere {
  position: fixed;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #007AFF, #00BCFF);
  box-shadow: 0 4px 12px rgba(0, 122, 255, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  transition:
      right 0.3s cubic-bezier(0.18, 0.89, 0.32, 1.28),
      bottom 0.3s cubic-bezier(0.18, 0.89, 0.32, 1.28),
      transform 0.2s ease,
      opacity 0.2s ease;
}

.float-sphere.dragging {
  opacity: 0.9;
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(0, 122, 255, 0.6);
  transition: none; /* 拖动时取消过渡效果 */
}

.sphere-icon {
  width: 28px;
  height: 28px;
  filter: brightness(0) invert(1);
}
</style>