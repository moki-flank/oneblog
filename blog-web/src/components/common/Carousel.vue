<template>
  <div class="carousel" v-if="slides && slides.length">
    <div class="carousel-container" :style="containerStyle">
      <div 
        v-for="(slide, index) in slides" 
        :key="index"
        class="carousel-slide"
        :class="{ active: currentIndex === index }"
      >
        <img v-lazy="slide.cover" :key="slide.cover" :alt="slide.title">
        <div class="slide-content">
          <h3>{{ slide.title }}</h3>
          <p>{{ slide.description }}</p>
          <button class="read-more" @click="$emit('click', slide.id)">
            阅读更多
            <i class="fas fa-arrow-right"></i>
          </button>
        </div>
      </div>
    </div>
    <button class="carousel-btn prev" @click="prev">
      <i class="fas fa-chevron-left"></i>
    </button>
    <button class="carousel-btn next" @click="next">
      <i class="fas fa-chevron-right"></i>
    </button>
    <div class="carousel-dots">
      <button 
        v-for="(_, index) in slides" 
        :key="index"
        class="dot"
        :class="{ active: currentIndex === index }"
        @click="goToSlide(index)"
      ></button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Carousel',
  props: {
    slides: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      currentIndex: 0,
      autoplayInterval: null
    }
  },
  computed: {
    containerStyle() {
      return {
        transform: `translateX(-${this.currentIndex * 100}%)`
      }
    }
  },
  methods: {
    /**
     * 下一页
     */
    next() {
      this.currentIndex = (this.currentIndex + 1) % this.slides.length
    },
    /**
     * 上一页
     */
    prev() {
      this.currentIndex = this.currentIndex === 0 
        ? this.slides.length - 1 
        : this.currentIndex - 1
    },
    /**
     * 跳转
     */
    goToSlide(index) {
      this.currentIndex = index
    },
    /**
     * 开始自动播放
     */
    startAutoplay() {
      this.autoplayInterval = setInterval(this.next, 5000)
    },
    /**
     * 停止自动播放
     */
    stopAutoplay() {
      clearInterval(this.autoplayInterval)
    }
  },
  mounted() {
    this.startAutoplay()
  },
  beforeDestroy() {
    this.stopAutoplay()
  }
}
</script>

<style lang="scss" scoped>
.carousel {
  position: relative;
  width: 100%;
  height: 380px;
  border-radius: $border-radius-lg;
  overflow: hidden;
  box-shadow: $shadow-lg;

  &:hover {
    .carousel-btn {
      opacity: 1;
    }
  }
}

.carousel-container {
  display: flex;
  height: 100%;
  transition: transform 0.5s ease;
}

.carousel-slide {
  flex: 0 0 100%;
  position: relative;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .slide-content {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: $spacing-xl;
    background: linear-gradient(
      transparent,
      rgba(0, 0, 0, 0.2) 20%,
      rgba(0, 0, 0, 0.8)
    );
    color: white;

    h3 {
      font-size: 2.2em;
      margin-bottom: $spacing-md;
      font-weight: 600;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    }

    p {
      margin-bottom: $spacing-lg;
      opacity: 0.9;
      font-size: 1.2em;
      max-width: 800px;
    }

    .read-more {
      display: inline-flex;
      align-items: center;
      gap: $spacing-sm;
      padding: $spacing-sm $spacing-lg;
      background: $primary;
      color: white;
      border: none;
      border-radius: 20px;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        background: darken($primary, 10%);
        transform: translateX(5px);
      }
    }
  }
}

.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255, 255, 255, 0.2);
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  color: white;
  cursor: pointer;
  opacity: 0;
  transition: all 0.3s ease;
  backdrop-filter: blur(4px);

  &:hover {
    background: rgba(255, 255, 255, 0.4);
    transform: translateY(-50%) scale(1.1);
  }

  &.prev {
    left: $spacing-lg;
  }

  &.next {
    right: $spacing-lg;
  }
}

.carousel-dots {
  position: absolute;
  bottom: $spacing-lg;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: $spacing-sm;

  .dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.5);
    border: none;
    cursor: pointer;
    transition: all 0.3s ease;

    &.active {
      background: white;
      transform: scale(1.2);
    }
  }
}
</style> 