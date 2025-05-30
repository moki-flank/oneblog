<template>
    <div class="slider-captcha">
      <div class="slider-track">
        <!-- 进度条 -->
        <div class="slider-bar" :style="{ width: `${sliderLeft}px` }">
          <div class="bar-light"></div>
        </div>
        <!-- 滑块按钮 -->
        <div 
          class="slider-button"
          :class="{ 'success': verified, 'error': verifyError }"
          :style="{ left: `${sliderLeft}px` }"
          @mousedown="handleDragStart"
          @touchstart="handleDragStart"
        >
          <div class="button-inner">
            <el-icon v-if="!verified && !verifyError"><DArrowRight /></el-icon>
            <el-icon v-else-if="verified"><Select /></el-icon>
            <el-icon v-else><Close /></el-icon>
          </div>
        </div>
        <!-- 提示文本 -->
        <div class="slider-text" :class="{ 'success': verified, 'error': verifyError }">
          {{ sliderText }}
        </div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, computed } from 'vue'
  import { DArrowRight, Select, Close } from '@element-plus/icons-vue'
  
  const emit = defineEmits(['success'])
  
  const verified = ref(false)
  const verifyError = ref(false)
  const sliderLeft = ref(0)
  const startX = ref(0)
  
  // 滑块文本
  const sliderText = computed(() => {
    if (verified.value) return '验证通过'
    if (verifyError.value) return '验证失败'
    return '向右滑动完成验证'
  })
  
  // 处理滑动开始
  const handleDragStart = (e: MouseEvent | TouchEvent) => {
    if (verified.value) return
    
    document.addEventListener('mousemove', handleDragMove)
    document.addEventListener('mouseup', handleDragEnd)
    document.addEventListener('touchmove', handleDragMove)
    document.addEventListener('touchend', handleDragEnd)
    
    startX.value = e instanceof MouseEvent ? e.clientX : e.touches[0].clientX
  }
  
  // 处理滑动过程
  const handleDragMove = (e: MouseEvent | TouchEvent) => {
    const currentX = e instanceof MouseEvent ? e.clientX : e.touches[0].clientX
    const moveX = currentX - startX.value
    const maxWidth = document.querySelector('.slider-track')?.clientWidth || 0
    
    sliderLeft.value = Math.min(Math.max(0, moveX), maxWidth - 40)
  }
  
  // 处理滑动结束
  const handleDragEnd = async () => {
    document.removeEventListener('mousemove', handleDragMove)
    document.removeEventListener('mouseup', handleDragEnd)
    document.removeEventListener('touchmove', handleDragMove)
    document.removeEventListener('touchend', handleDragEnd)
    
    const maxWidth = document.querySelector('.slider-track')?.clientWidth || 0
    const threshold = maxWidth - 45 // 设置阈值，接近最大宽度即视为成功
    
    if (sliderLeft.value >= threshold) {
      verified.value = true
      emit('success')
    } else {
      verifyError.value = true
      setTimeout(() => {
        verifyError.value = false
        sliderLeft.value = 0
      }, 1000)
    }
  }
  
  // 重置验证码
  const reset = () => {
    verified.value = false
    verifyError.value = false
    sliderLeft.value = 0
  }
  
  // 暴露方法
  defineExpose({
    reset
  })
  </script>
  
  <style scoped>
  .slider-captcha {
    width: 100%;
  }
  
  /* 滑动轨道样式 */
  .slider-track {
    position: relative;
    height: 36px;
    background: var(--el-fill-color-light);
    border-radius: 18px;
    overflow: hidden;
  }
  
  /* 进度条样式 */
  .slider-bar {
    position: absolute;
    height: 100%;
    background: linear-gradient(90deg, 
      var(--el-color-primary), 
      var(--el-color-primary-light-3)
    );
    transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
  
  /* 进度条光效 */
  .bar-light {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(
      90deg,
      transparent,
      rgba(255, 255, 255, 0.3),
      transparent
    );
    animation: lightMove 2s linear infinite;
  }
  
  @keyframes lightMove {
    from { transform: translateX(-100%); }
    to { transform: translateX(100%); }
  }
  
  /* 滑块按钮样式 */
  .slider-button {
    position: absolute;
    top: 2px;
    width: 32px;
    height: 32px;
    background: white;
    border-radius: 50%;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    transition: all 0.3s;
    user-select: none;
  }
  
  /* 按钮内部样式 */
  .button-inner {
    font-size: 16px;
    color: var(--el-text-color-secondary);
    transition: all 0.3s;
  }
  
  .slider-button:hover {
    transform: scale(1.05);
    box-shadow: 0 3px 8px rgba(0, 0, 0, 0.15);
  }
  
  /* 成功状态 */
  .slider-button.success {
    background: #67c23a;
  }
  
  .slider-button.success .button-inner {
    color: white;
  }
  
  /* 错误状态 */
  .slider-button.error {
    background: #f56c6c;
  }
  
  .slider-button.error .button-inner {
    color: white;
  }
  
  /* 文本提示样式 */
  .slider-text {
    position: absolute;
    width: 100%;
    text-align: center;
    line-height: 36px;
    color: var(--el-text-color-secondary);
    font-size: 14px;
    user-select: none;
    pointer-events: none;
  }
  
  .slider-text.success {
    color: #67c23a;
  }
  
  .slider-text.error {
    color: #f56c6c;
  }
  
  /* 深色模式适配 */
  :deep(.dark) {
    .slider-track {
      background: var(--el-fill-color-dark);
    }
  
    .slider-button {
      background: var(--el-bg-color);
    }
  }
  </style> 