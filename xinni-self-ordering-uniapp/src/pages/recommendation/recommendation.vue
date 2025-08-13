<template>
  <view class="container">
    <view class="chat-container">
      <!-- 聊天记录 -->
      <scroll-view class="chat-history" scroll-y="true" :scroll-top="scrollTop">
        <view class="message-list">
          <view
            v-for="(message, index) in messages"
            :key="index"
            :class="['message', message.type]"
          >
            <view class="message-content">
              <view class="avatar">
                <image
                  v-if="message.type === 'user'"
                  src="/static/images/user_default.png"
                  mode="aspectFill"
                  class="avatar-img"
                />
                <view v-else class="ai-avatar">AI</view>
              </view>
              <view class="text-content">
                <view class="text" v-if="message.type === 'ai'">
                  <rich-text :nodes="formatAiMessage(message.content)"></rich-text>
                </view>
                <view class="text" v-else>
                  {{ message.content }}
                </view>
              </view>
            </view>
          </view>
          <!-- 加载状态 -->
          <view v-if="loading" class="message ai">
            <view class="message-content">
              <view class="avatar">
                <view class="ai-avatar">AI</view>
              </view>
              <view class="text-content">
                <view class="text">
                  <view class="loading-dots">
                    <view class="dot"></view>
                    <view class="dot"></view>
                    <view class="dot"></view>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </scroll-view>

      <!-- 输入区域 -->
      <view class="input-container">
        <input 
          class="message-input"
          type="text"
          v-model="userQuery" 
          placeholder="请输入您的用餐需求，例如：想吃辣的"
          @confirm="sendMessage"
        />
        <button class="send-btn" @click="sendMessage" :disabled="loading">
          发送
        </button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue'
import { getDishRecommendationAPI } from '@/api/recommendation'

// 定义消息类型
interface Message {
  type: 'user' | 'ai'
  content: string
}

// 定义聊天记录存储结构
interface ChatHistory {
  messages: Message[]
  timestamp: number
}

const userQuery = ref('')
const messages = ref<Message[]>([])
const loading = ref(false)
const scrollTop = ref(0)

// 组件挂载时从本地存储恢复聊天记录
onMounted(() => {
  loadChatHistory()
})

// 从本地存储加载聊天记录
const loadChatHistory = () => {
  try {
    const savedData = uni.getStorageSync('chatHistory')

    // 检查是否有保存的数据
    if (savedData) {
      const chatHistory: ChatHistory = savedData

      // 检查数据是否过期（7天）
      const now = Date.now()
      const sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000 // 7天的毫秒数

      if (now - chatHistory.timestamp < sevenDaysInMillis) {
        // 数据未过期，使用保存的聊天记录
        messages.value = chatHistory.messages
        return
      } else {
        // 数据已过期，清除过期数据
        uni.removeStorageSync('chatHistory')
      }
    }

    // 没有保存的数据或数据已过期，设置默认欢迎消息
    messages.value = [
      {
        type: 'ai',
        content: '您好！我是您的专属菜品推荐助手，请告诉我您的用餐需求，例如口味偏好、用餐人数、场合等，我会为您推荐合适的菜品。'
      }
    ]
  } catch (error) {
    console.error('加载聊天历史失败:', error)
    // 出错时设置默认欢迎消息
    messages.value = [
      {
        type: 'ai',
        content: '您好！我是您的专属菜品推荐助手，请告诉我您的用餐需求，例如口味偏好、用餐人数、场合等，我会为您推荐合适的菜品。'
      }
    ]
  }
}

// 保存聊天记录到本地存储（带时间戳）
const saveChatHistory = () => {
  try {
    const chatHistory: ChatHistory = {
      messages: messages.value,
      timestamp: Date.now()
    }
    uni.setStorageSync('chatHistory', chatHistory)
  } catch (error) {
    console.error('保存聊天历史失败:', error)
  }
}

// 发送消息
const sendMessage = async () => {
  if (!userQuery.value.trim()) {
    uni.showToast({
      title: '请输入用餐需求',
      icon: 'none'
    })
    return
  }

  // 添加用户消息
  messages.value.push({
    type: 'user',
    content: userQuery.value
  })

  // 保存聊天记录
  saveChatHistory()

  // 清空输入框
  const query = userQuery.value
  userQuery.value = ''
  
  // 滚动到底部
  await scrollToBottom()
  
  try {
    loading.value = true

    // 获取AI推荐
    const res = await getDishRecommendationAPI(query)

    // 添加AI回复
    messages.value.push({
      type: 'ai',
      content: res.data
    })

    // 保存聊天记录
    saveChatHistory()
  } catch (error) {
    messages.value.push({
      type: 'ai',
      content: '抱歉，获取推荐失败，请稍后再试~'
    })
    // 保存聊天记录
    saveChatHistory()
    console.error(error)
  } finally {
    loading.value = false
    // 滚动到底部
    scrollToBottom()
  }
}

// 格式化AI消息为富文本
const formatAiMessage = (content: string) => {
  if (!content) return ''

  // 将换行符转换为<br>标签
  return content
    .replace(/\n/g, '<br>')
    .replace(/### (.*?)(<br>|$)/g, '<h3 style="margin: 10px 0 5px; font-size: 16px; color: #333;">$1</h3>')
    .replace(/\*\*([^*]+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\* ([^<]*?)(<br>|$)/g, '<view style="display: flex; align-items: flex-start; margin: 5px 0;"><view style="width: 4px; height: 4px; border-radius: 50%; background: #666; margin: 8px 8px 0 0;"></view><view style="flex: 1;">$1</view></view>$2')
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  scrollTop.value = 999999
}
</script>

<style scoped>
.container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.chat-container {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.chat-history {
  flex: 1;
  background-color: #f5f5f5;
  margin-top: 10px;
  overflow-y: auto;
}

.message-list {
  padding-bottom: 20px;
}

.message {
  margin-bottom: 20px;
}

.message-content {
  display: flex;
  align-items: flex-start;
}

.message.user {
  align-items: flex-end;
}

.message.user .message-content {
  flex-direction: row-reverse;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 10px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-img {
  width: 100%;
  height: 100%;
}

.ai-avatar {
  width: 100%;
  height: 100%;
  background-color: #007aff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  border-radius: 50%;
}

.text-content {
  max-width: 70%;
}

.message.ai .text-content {
  background: white;
  border-radius: 0 12px 12px 12px;
}

.message.user .text-content {
  background: #007aff;
  border-radius: 12px 0 12px 12px;
  color: white;
}

.text {
  padding: 12px;
  line-height: 1.5;
  font-size: 14px;
  word-wrap: break-word;
}

.message.user .text {
  color: white;
}

.message.ai .text {
  color: #333;
}

.input-container {
  display: flex;
  padding: 10px 16px;
  background: white;
  border-top: 1px solid #eee;
  flex-shrink: 0;
}

.message-input {
  flex: 1;
  border: 1px solid #eee;
  padding: 10px;
  border-radius: 20px;
  margin-right: 10px;
  font-size: 14px;
}

.send-btn {
  padding: 0 20px;
  background: #007aff;
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 14px;
}

.send-btn[disabled] {
  background: #cccccc;
}

.loading-dots {
  display: flex;
  align-items: center;
}

.dot {
  width: 6px;
  height: 6px;
  background: #999;
  border-radius: 50%;
  margin: 0 2px;
  animation: bounce 1.5s infinite;
}

.dot:nth-child(2) {
  animation-delay: 0.2s;
}

.dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}
</style>
