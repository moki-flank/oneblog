<template>
    <div>
        <main class="app-content">
            <Announcement />
            <!-- 内容 -->
            <transition name="page" mode="out-in">
                <router-view :key="$route.fullPath"></router-view>
            </transition>
        </main>
    </div>
  </template>

  <script>
    import Announcement from '@/components/Announcement/index.vue';
    export default {
        components: {
            Announcement
        }
    }
  </script>
<style lang="scss">
    // 页面过渡动画
.page-enter-active {
  animation: fade-in 0.3s;
}
.page-leave-active {
  animation: fade-out 0.3s;
}

@keyframes fade-in {
  0% {
    opacity: 0;
    transform: scale(0.95);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes fade-out {
  0% {
    opacity: 1;
    transform: scale(1);
  }
  100% {
    opacity: 0;
    transform: scale(1.05);
  }
}
</style>
