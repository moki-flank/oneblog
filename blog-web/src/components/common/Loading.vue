<template>
  <div class="loading-wrapper" v-show="visible" :class="{ 'local': local }">
    <div class="loading-spinner">
      <div class="galaxy">
        <div class="ring"></div>
        <div class="ring"></div>
        <div class="ring"></div>
      </div>
      <p class="loading-text">Loading...</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Loading',
  props: {
    local: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      visible: false,
      minDisplayTime: 300, // 最小显示时间（毫秒）
      showStartTime: 0
    }
  },
  watch: {
    visible(val) {
      if (!this.local) {
        if (val) {
          document.body.style.overflow = 'hidden'
          this.showStartTime = Date.now()
        } else {
          const elapsedTime = Date.now() - this.showStartTime
          if (elapsedTime < this.minDisplayTime) {
            setTimeout(() => {
              document.body.style.overflow = ''
            }, this.minDisplayTime - elapsedTime)
          } else {
            document.body.style.overflow = ''
          }
        }
      }
    }
  },
  beforeDestroy() {
    if (!this.local) {
      document.body.style.overflow = ''
    }
  }
}
</script>

<style lang="scss" scoped>
.loading-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(4px);
  background: linear-gradient(135deg, #f5f7fa, #c3cfe2);
  opacity: 0.95;

  &.local {
    position: absolute !important;
    backdrop-filter: blur(2px);
    z-index: 100;
    width: 100%;
    height: 100%;
    background: var(--loading-bg);
    opacity: 0.8;
  }
}

.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  animation: spinnerFadeIn 0.5s ease-out;
  
  .local & {
    transform: scale(0.8);
  }
}

.loading-text {
  color: $primary;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 2px;
  animation: pulse 1.5s ease-in-out infinite;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2);
  transition: color 0.3s ease;
}

@keyframes spinnerFadeIn {
  from { opacity: 0; transform: scale(0.5); }
  to { opacity: 1; transform: scale(1); }
}

.galaxy {
  position: relative;
  width: 80px;
  height: 80px;
  transform-style: preserve-3d;
  perspective: 1000px;
  animation: galaxySpin 3s linear infinite;
}

@keyframes galaxySpin {
  from { transform: rotateY(0deg); }
  to { transform: rotateY(360deg); }
}

.ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 2px solid transparent;
  border-radius: 50%;
  
  &:nth-child(1) {
    border-top: 2px solid $primary;
    animation: rotate1 2s linear infinite;
  }
  
  &:nth-child(2) {
    border-right: 2px solid $primary;
    animation: rotate2 2s linear infinite;
  }
  
  &:nth-child(3) {
    border-bottom: 2px solid $primary;
    animation: rotate3 2s linear infinite;
  }
}

@keyframes rotate1 {
  0% { transform: rotateX(35deg) rotateY(-45deg) rotateZ(0); }
  100% { transform: rotateX(35deg) rotateY(-45deg) rotateZ(360deg); }
}

@keyframes rotate2 {
  0% { transform: rotateX(50deg) rotateY(10deg) rotateZ(0); }
  100% { transform: rotateX(50deg) rotateY(10deg) rotateZ(360deg); }
}

@keyframes rotate3 {
  0% { transform: rotateX(-40deg) rotateY(25deg) rotateZ(0); }
  100% { transform: rotateX(-40deg) rotateY(25deg) rotateZ(360deg); }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
</style> 