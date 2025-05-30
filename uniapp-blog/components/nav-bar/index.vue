<template>
  <view class="nav-bar" :style="{ height: navBarHeight + 'px', background: background }">
    <view class="nav-content" :style="{ height: contentHeight + 'px', top: statusBarHeight + 'px' }">
      <view class="left">
        <view class="btn-group">
          <view class="nav-btn" @click="goBack" v-if="showBack">
            <u-icon name="arrow-left" :color="iconColor" size="25"></u-icon>
          </view>
          <!-- 分割线 -->
          <view class="dvier" />
          <view class="nav-btn" @click="goHome" v-if="showHome">
            <u-icon name="home-fill" :color="iconColor" size="25"></u-icon>
          </view>
        </view>
      </view>
      <view class="center" v-if="title">
        <text class="title" :style="{ color: titleColor }">{{title}}</text>
      </view>
      <view class="right">
        <slot name="right"></slot>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'nav-bar',
  props: {
    title: {
      type: String,
      default: ''
    },
    titleColor: {
      type: String,
      default: '#ffffff'
    },
    background: {
      type: String,
      default: 'linear-gradient(to bottom, rgba(0,0,0,0.5), rgba(0,0,0,0))'
    },
    iconColor: {
      type: String,
      default: '#ffffff'
    },
    showBack: {
      type: Boolean,
      default: true
    },
    showHome: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      statusBarHeight: 20,
      contentHeight: 44,
      navBarHeight: 64
    }
  },
  created() {
    // 获取状态栏高度
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight
    this.contentHeight = 44
    this.navBarHeight = this.statusBarHeight + this.contentHeight
  },
  methods: {
    goBack() {
      const pages = getCurrentPages()
      if (pages.length > 1) {
        uni.navigateBack({ delta: 1 })
      } else {
        this.goHome()
      }
    },
    goHome() {
      uni.switchTab({
        url: '/pages/home/index'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;

  .nav-content {
    position: relative;
    display: flex;
    align-items: center;
    padding: 0 24rpx;

    .left {
      position: relative;
      z-index: 2;

      .btn-group {
        display: flex;
        align-items: center;
        background: rgba(255, 255, 255, 0.1);
        border-radius: 32rpx;
        padding: 2rpx;
        backdrop-filter: blur(10px);
        -webkit-backdrop-filter: blur(10px);
        box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);

       
        .nav-btn {
          width: 60rpx;
          height: 60rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          position: relative;
          border-radius: 30rpx;
          transition: all 0.2s ease;
   
          &:active {
            transform: scale(0.95);
            background: rgba(255, 255, 255, 0.1);
          }

          & + .nav-btn {
            margin-left: 2rpx;
          }
        }
        .dvier{
          width: 5rpx;
          height: 30rpx;
          margin: 0 10px;
          background-color: #ffffffb0;
        }
      }
    }

    .center {
      position: absolute;
      left: 0;
      right: 0;
      text-align: center;
      z-index: 1;

      .title {
        font-size: 30rpx;
        font-weight: 500;
        text-shadow: 0 1rpx 2rpx rgba(0, 0, 0, 0.1);
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 1;
        overflow: hidden;
        padding: 0 160rpx;
      }
    }

    .right {
      position: relative;
      z-index: 2;
    }
  }
}

@media (prefers-color-scheme: dark) {
  .nav-bar {
    .nav-content {
      .left {
        .btn-group {
          background: rgba(255, 255, 255, 0.08);
          box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.15);
        }
      }
    }
  }
}
</style> 