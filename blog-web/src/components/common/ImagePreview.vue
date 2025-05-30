<template>
  <transition name="fade">
    <div 
      class="image-preview" 
      v-show="visible" 
      @click="close"
      @mousewheel.prevent="handleWheel"
      @mousemove="onDrag"
      @mouseup="stopDrag"
      @keydown.left="prev"
      @keydown.right="next"
      @touchstart="startTouch"
      @touchmove.prevent="onTouch"
      @touchend="stopTouch"
      tabindex="-1"
    >
      <div class="preview-wrapper">
        <img 
          :src="currentImage" 
          :style="{ 
            transform: `translate(${position.x}px, ${position.y}px) scale(${scale}) rotate(${rotation}deg)` 
          }"
          @click.stop
          @mousedown.stop.prevent="startDrag"
          @touchstart.stop="startTouch"
        >
      </div>
      
      <div class="nav-buttons" v-if="images.length > 1">
        <button class="nav-btn prev" @click.stop="prev" :disabled="currentIndex <= 0">
          <i class="fas fa-chevron-left"></i>
        </button>
        <button class="nav-btn next" @click.stop="next" :disabled="currentIndex >= images.length - 1">
          <i class="fas fa-chevron-right"></i>
        </button>
      </div>
      
      <div class="preview-toolbar" @click.stop>
        <button class="tool-btn" @click="rotate(-90)">
          <i class="fas fa-undo"></i>
        </button>
        <button class="tool-btn" @click="rotate(90)">
          <i class="fas fa-redo"></i>
        </button>
        <button class="tool-btn" @click="zoom(0.1)">
          <i class="fas fa-search-plus"></i>
        </button>
        <button class="tool-btn" @click="zoom(-0.1)">
          <i class="fas fa-search-minus"></i>
        </button>
        <button class="tool-btn" @click="reset">
          <i class="fas fa-sync-alt"></i>
        </button>
        <button class="tool-btn" @click="close">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div class="image-counter" v-if="images.length > 1">
        {{ currentIndex + 1 }} / {{ images.length }}
      </div>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'ImagePreview',
  data() {
    return {
      visible: false,
      images: [],
      currentIndex: 0,
      scale: 1,
      rotation: 0,
      position: { x: 0, y: 0 },
      isDragging: false,
      lastMousePosition: { x: 0, y: 0 }
    }
  },
  computed: {
    currentImage() {
      return this.images[this.currentIndex] || ''
    }
  },
  methods: {
    /**
     * 显示图片预览
     * @param images 图片数组
     * @param startIndex 开始显示的索引
     */
    show(images, startIndex = 0) {
      this.images = Array.isArray(images) ? images : [images]
      this.currentIndex = startIndex
      this.visible = true
      this.reset()
      document.body.style.overflow = 'hidden'
      this.$nextTick(() => {
        this.$el.focus()
      })
    },
    /**
     * 关闭图片预览
     */
    close() {
      this.visible = false
      document.body.style.overflow = ''
    },
    /**
     * 显示上一张图片
     */
    prev() {
      if (this.currentIndex > 0) {
        this.currentIndex--
        this.reset()
      }
    },
    /**
     * 显示下一张图片
     */
    next() {
      if (this.currentIndex < this.images.length - 1) {
        this.currentIndex++
        this.reset()
      }
    },
    /**
     * 旋转图片
     * @param deg 旋转角度
     */
    rotate(deg) {
      this.rotation = (this.rotation + deg) % 360
    },
    /**
     * 缩放图片
     * @param delta 缩放比例
     */
    zoom(delta) {
      const newScale = this.scale + delta
      if (newScale >= 0.1 && newScale <= 3) {
        this.scale = newScale
      }
    },
    /**
     * 重置图片
     */
    reset() {
      this.scale = 1
      this.rotation = 0
      this.position = { x: 0, y: 0 }
    },
    /**
     * 处理鼠标滚轮事件
     * @param e 鼠标滚轮事件
     */
    handleWheel(e) {
      const delta = e.deltaY > 0 ? -0.1 : 0.1
      this.zoom(delta)
    },
    /**
     * 开始拖拽
     * @param e 鼠标事件
     */
    startDrag(e) {
      this.isDragging = true
      this.lastMousePosition = {
        x: e.clientX,
        y: e.clientY
      }
    },
    /**
     * 拖拽图片
     * @param e 鼠标事件
     */
    onDrag(e) {
      if (!this.isDragging) return
      
      const deltaX = e.clientX - this.lastMousePosition.x
      const deltaY = e.clientY - this.lastMousePosition.y
      
      this.position.x += deltaX
      this.position.y += deltaY
      
      this.lastMousePosition = {
        x: e.clientX,
        y: e.clientY
      }
    },
    /**
     * 停止拖拽
     */
    stopDrag() {
      this.isDragging = false
    },
    /**
     * 开始触摸
     * @param e 触摸事件
     */
    startTouch(e) {
      if (e.target.tagName.toLowerCase() === 'img' && e.touches.length === 1) {
        e.preventDefault()
        this.isDragging = true
        this.lastMousePosition = {
          x: e.touches[0].clientX,
          y: e.touches[0].clientY
        }
      }
    },
    /**
     * 触摸事件
     * @param e 触摸事件
     */
    onTouch(e) {
      if (!this.isDragging || e.touches.length !== 1) return
      
      const deltaX = e.touches[0].clientX - this.lastMousePosition.x
      const deltaY = e.touches[0].clientY - this.lastMousePosition.y
      
      this.position.x += deltaX
      this.position.y += deltaY
      
      this.lastMousePosition = {
        x: e.touches[0].clientX,
        y: e.touches[0].clientY
      }
    },
    /**
     * 停止触摸
     */
    stopTouch() {
      this.isDragging = false
    }
  },
  mounted() {
    // 监听ESC键盘事件
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') {
        this.close()
      }
    })
  },
  beforeDestroy() {
    // 移除ESC键盘事件监听
    document.removeEventListener('keydown', (e) => {
      if (e.key === 'Escape') {
        this.close()
      }
    })
  }
}
</script>

<style lang="scss" scoped>
.image-preview {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .preview-wrapper {
    max-width: 90vw;
    max-height: 90vh;
    
    img {
      max-width: 100%;
      max-height: 90vh;
      object-fit: contain;
      cursor: grab;
      user-select: none;
      
      &:active {
        cursor: grabbing;
      }
    }
  }
  
  .preview-toolbar {
    position: fixed;
    bottom: 30px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    gap: 10px;
    padding: 10px;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border-radius: 8px;
    
    .tool-btn {
      width: 40px;
      height: 40px;
      border: none;
      background: rgba(255, 255, 255, 0.2);
      color: white;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.2s ease;
      
      &:hover {
        background: rgba(255, 255, 255, 0.3);
        transform: translateY(-2px);
      }
      
      i {
        font-size: 16px;
      }
    }
  }
}

.fade-enter-active {
  animation: fadeIn 0.3s ease;
  
  img {
    animation: zoomIn 0.3s ease;
  }
}

.fade-leave-active {
  animation: fadeOut 0.3s ease;
  
  img {
    animation: zoomOut 0.3s ease;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes fadeOut {
  from {
    opacity: 1;
  }
  to {
    opacity: 0;
  }
}

@keyframes zoomIn {
  from {
    opacity: 0;
    transform: translate(0, 0) scale(0.3) rotate(0deg);
  }
  to {
    opacity: 1;
    transform: translate(0, 0) scale(1) rotate(0deg);
  }
}

@keyframes zoomOut {
  from {
    opacity: 1;
    transform: translate(0, 0) scale(1) rotate(0deg);
  }
  to {
    opacity: 0;
    transform: translate(0, 0) scale(0.3) rotate(0deg);
  }
}

.nav-buttons {
  position: fixed;
  top: 50%;
  left: 0;
  right: 0;
  transform: translateY(-50%);
  display: flex;
  justify-content: space-between;
  padding: 0 20px;
  pointer-events: none;

  .nav-btn {
    width: 44px;
    height: 44px;
    border: none;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.2);
    color: white;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
    pointer-events: auto;
    
    &:hover:not(:disabled) {
      background: rgba(255, 255, 255, 0.3);
      transform: scale(1.1);
    }
    
    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
    
    i {
      font-size: 20px;
    }
  }
}

.image-counter {
  position: fixed;
  bottom: 100px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.5);
  color: white;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 14px;
}
</style> 