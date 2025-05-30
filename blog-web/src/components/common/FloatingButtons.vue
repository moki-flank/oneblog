<template>
  <div class="floating-buttons" :class="{ 'show-top': showBackToTop }">

    <el-tooltip content="聊天" placement="left">
      <router-link to="/chat" class="float-btn chat-btn" title="聊天">
      <i class="fas fa-comments"></i>
    </router-link>
    </el-tooltip>
 
    <el-tooltip content="切换主题" placement="left">
      <button class="float-btn theme-btn" @click="toggleTheme" title="切换主题">
        <i :class="['fas', isDarkMode ? 'fa-sun' : 'fa-moon']"></i>
      </button>
    </el-tooltip>

    <el-tooltip content="回到顶部" placement="left">
      <button 
        v-show="showBackToTop"
        class="float-btn top-btn" 
        @click="scrollToTop"
        title="回到顶部"
      >
        <i class="fas fa-arrow-up"></i>
      </button>
    </el-tooltip>
  </div>
</template>

<script>
import { getThemeMode, setThemeMode, initTheme } from '@/utils/theme'

export default {
  name: 'FloatingButtons',
  data() {
    return {
      isDarkMode: false,
      showBackToTop: false
    }
  },
  computed: {
    themeIcon() {
      return ['fas', this.isDarkMode ? 'fa-sun' : 'fa-moon']
    }
  },
  methods: {
    toggleTheme() {
      this.isDarkMode = !this.isDarkMode
      const mode = this.isDarkMode ? 'dark' : 'light'
      setThemeMode(mode)
    },
    scrollToTop() {
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      })
    },
    handleScroll() {
      this.showBackToTop = window.pageYOffset > 300
    }
  },
  mounted() {
    this.isDarkMode = initTheme()
    window.addEventListener('scroll', this.handleScroll)
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.handleScroll)
  }
}
</script>

<style lang="scss" scoped>
.floating-buttons {
  position: fixed;
  right: 20px;
  bottom: 100px;
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;
  z-index: 1000;
}

.float-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 50%;
  background: var(--card-bg);
  color: var(--text-primary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: $shadow-lg;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  opacity: 0.8;

  &:hover {
    opacity: 1;
    transform: translateY(-2px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  }

  &.chat-btn {
    background: $primary;
    color: white;
    text-decoration: none;
    animation: pulse 2s infinite;
    &:hover {
      background: darken($primary, 10%);
    }
  }

  &.theme-btn {
    background: linear-gradient(135deg, $primary, $secondary);
    color: white;
   
  }
  

  &.top-btn {
    transform: translateY(100px);
    opacity: 0;
    visibility: hidden;
    background: var(--card-bg);
    border: 1px solid var(--border-color);

    .show-top & {
      transform: translateY(0);
      opacity: 0.8;
      visibility: visible;
      animation: bounceIn 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55);
    }
  }

  i {
    font-size: 1.2em;
  }
}

@keyframes bounceIn {
  0% {
    transform: translateY(100px) scale(0.3);
    opacity: 0;
  }
  50% {
    transform: translateY(-10px) scale(1.1);
  }
  70% {
    transform: translateY(5px) scale(0.95);
  }
  100% {
    transform: translateY(0) scale(1);
    opacity: 0.8;
  }
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba($primary, 0.4);
  }
  70% {
    box-shadow: 0 0 0 10px rgba($primary, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba($primary, 0);
  }
}

@include responsive(sm) {
  .floating-buttons {
    right: 15px;
    bottom: 80px;
  }

  .float-btn {
    width: 36px;
    height: 36px;
  }
}


</style> 