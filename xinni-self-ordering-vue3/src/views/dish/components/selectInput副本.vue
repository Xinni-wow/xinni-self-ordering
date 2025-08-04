<template>
    <div class="selectInput">
      <el-input
        v-model="keyValue"
        type="text"
        placeholder="请选择或自定义口味名"
        clearable
        :readonly="!customizing"
        @focus="selectFlavor(true)"
        @blur="handleBlur"
        @change="handleManualInput"
      />
      <div v-if="mak && dishFlavorsData.length" class="flavorSelect">
        <span
          v-for="(item, index) in dishFlavorsData"
          :key="index"
          class="items"
          @mousedown.prevent="checkOption(item.name, index)"
        >
          {{ item.name }}
        </span>
        <span class="items custom-item" @mousedown.prevent="chooseCustom">
          ➕ 自定义口味名
        </span>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, watch, computed } from 'vue'
  
  interface DishFlavorName {
    name: string;
  }
  
  const props = defineProps({
    dishFlavorsData: {
      type: Array as () => DishFlavorName[],
      default: () => [],
    },
    value: {
      type: [String, Number],
      default: '',
    },
    index: {
      type: Number,
      default: 0,
    },
  })
  
  const emit = defineEmits(["select"])
  
  const keyValue = ref(props.value)
  const customizing = ref(false) // 是否处于“自定义口味名”模式
  const mak = ref(false) // 控制下拉显示
  
  watch(() => props.value, (newVal) => {
    keyValue.value = newVal
  })
  
  // 展开下拉
  const selectFlavor = (status: boolean) => {
    mak.value = status
  }
  
  // 延迟关闭下拉，避免选项被点击前就关闭
  const handleBlur = () => {
    setTimeout(() => {
      mak.value = false
    }, 200)
  }
  
  // 用户手动输入（仅在自定义模式下触发）
  const handleManualInput = () => {
    if (customizing.value) {
      emit('select', keyValue.value, props.index, -1)
    }
  }
  
  // 选择已有的口味名
  const checkOption = (name: string, index: number) => {
    customizing.value = false
    keyValue.value = name
    emit('select', name, props.index, index)
    mak.value = false
  }
  
  // 选择“自定义口味名”
  const chooseCustom = () => {
    customizing.value = true
    keyValue.value = ''
    emit('select', '', props.index, -1)
    mak.value = false
  }
  </script>
  
  <style lang="less" scoped>
  .selectInput {
    position: relative;
    width: 100%;
  
    .flavorSelect {
      position: absolute;
      top: 40px;
      width: 100%;
      background: #fff;
      border: 1px solid #ccc;
      z-index: 99;
      border-radius: 4px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  
      .items {
        padding: 8px 12px;
        cursor: pointer;
        font-size: 14px;
        border-bottom: 1px solid #f4f4f4;
  
        &:hover {
          background-color: #f5f7fa;
        }
      }
  
      .custom-item {
        color: #409EFF;
        font-weight: bold;
      }
    }
  }
  </style>
  