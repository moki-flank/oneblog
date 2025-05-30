<template>
  <view class="container">
    <!-- 背景装饰 -->
    <view class="bg-decoration"></view>

    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="user-info" v-if="isLogin">
        <image class="avatar" :src="userInfo.avatar" mode="aspectFill"></image>
        <view class="info-content">
          <text class="nickname">{{ userInfo.nickname }}</text>
          <text class="signature">{{ userInfo.signature }}</text>
        </view>
        <view class="edit-btn" @click="handleEdit">
          <u-icon name="edit-pen" size="28"></u-icon>
        </view>
      </view>

      <!-- 未登录状态 -->
      <view class="login-section" v-else>
        <image
          class="default-avatar"
          src="/static/tabbar/mine.png"
          mode="aspectFill"
        ></image>
        <text class="login-tip">登录体验更多功能</text>
        <button
          class="wx-login-btn"
          open-type="getUserInfo"
          @getuserinfo="handleWXLogin"
        >
          <u-icon name="weixin-fill" color="#fff" size="32"></u-icon>
          <text>微信一键登录</text>
        </button>
      </view>
    </view>

    <!-- 功能列表 -->
    <view class="feature-list">
      <view class="feature-item" @click="handleAboutUs">
        <view class="feature-content">
          <view class="icon-wrapper" style="background: #ff9a9e">
            <u-icon name="info" color="#fff" size="36"></u-icon>
          </view>
          <text class="feature-text">关于我们</text>
        </view>
      </view>

      <view class="feature-item" @click="handleOpenSource">
        <view class="feature-content">
          <view class="icon-wrapper" style="background: #66a6ff">
            <u-icon name="attach" color="#fff" size="36"></u-icon>
          </view>
          <text class="feature-text">开源地址</text>
        </view>
      </view>

      <button class="feature-item feedback-btn" open-type="feedback">
        <view class="feature-content">
          <view class="icon-wrapper" style="background: #43e97b">
            <u-icon name="email-fill" color="#fff" size="36"></u-icon>
          </view>
          <text class="feature-text">意见反馈</text>
        </view>
      </button>

      <button class="feature-item feedback-btn" open-type="contact">
        <view class="feature-content">
          <view class="icon-wrapper" style="background: #ffc107">
            <u-icon name="server-fill" color="#fff" size="36"></u-icon>
          </view>
          <text class="feature-text">合作勾搭</text>
        </view>
      </button>

      <view class="feature-item" @click="handleSettings">
        <view class="feature-content">
          <view class="icon-wrapper" style="background: #764ba2">
            <u-icon name="setting-fill" color="#fff" size="36"></u-icon>
          </view>
          <text class="feature-text">系统设置</text>
        </view>
      </view>

      <view class="feature-item" @click="handleLogout" v-if="isLogin">
        <view class="feature-content">
          <view class="icon-wrapper" style="background: #ff4757">
            <u-icon name="minus-people-fill" color="#fff" size="36"></u-icon>
          </view>
          <text class="feature-text">退出登录</text>
        </view>
      </view>
    </view>

    <!-- 版本信息 -->
    <view class="version-info">
      <text>当前版本 1.0.0</text>
    </view>
  </view>
</template>

<script>
import { loginApi } from "@/api/user";
export default {
  data() {
    return {
      isLogin: false,
      userInfo: {},
    };
  },
  methods: {
    handleAboutUs() {
      this.$u.toast("功能建设中...");
    },
    handleOpenSource() {
      // 复制开源地址到剪贴板
      uni.setClipboardData({
        data: "https://github.com/your-repo-url",
        success: () => {
          uni.showToast({
            title: "地址已复制",
            icon: "success",
          });
        },
      });
    },
    handleSettings() {
      this.$u.toast("功能建设中...");
    },
    /**
     * 微信登录
     */
    async handleWXLogin(e) {
      if (e.detail.errMsg !== "getUserInfo:ok") {
        uni.showToast({
          title: "登录失败",
          icon: "none",
        });
        return;
      }

      uni.showLoading({
        title: "授权登录中...",
      });

      try {
        const loginRes = await uni.login();
        if (loginRes[1].code) {
          const res = await loginApi(loginRes[1].code);
          if (res.code === 200) {
            uni.setStorageSync("token", res.data.token);
            this.userInfo = {
              id: res.data.id,
              avatar: res.data.avatar,
              nickname: res.data.nickname,
              signature: res.data.signature,
            };
            uni.setStorageSync("userInfo", this.userInfo);
            this.isLogin = true;
            uni.showToast({
              title: "登录成功",
              icon: "success",
            });
          }
          uni.hideLoading();
        }
      } catch (err) {
        uni.showToast({
          title: "登录失败，请重试",
          icon: "none",
        });
        uni.hideLoading();
      }
    },
    handleEdit() {
      uni.navigateTo({
        url: "/pages/user/edit",
      });
    },
    /**
     * 退出登录
     */
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            // 清除登录信息
            uni.removeStorageSync('token');
            uni.removeStorageSync('userInfo');
            this.isLogin = false;
            this.userInfo = {};
            
            uni.showToast({
              title: '已退出登录',
              icon: 'success'
            });
          }
        }
      });
    },
  },
  onLoad() {
    // 检查登录状态
    this.userInfo = uni.getStorageSync("userInfo");
    console.log(this.userInfo);
    this.isLogin = !!uni.getStorageSync("token");
  },
};
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #f5f6fa;
  padding-bottom: 40rpx;
  position: relative;
}

.bg-decoration {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 440rpx;
  background: linear-gradient(125deg, #2979ff 0%, #5e35b1 100%);
  border-bottom-left-radius: 60rpx;
  border-bottom-right-radius: 60rpx;

  &::after {
    content: "";
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 60rpx;
    background: linear-gradient(
      to bottom,
      rgba(245, 246, 250, 0) 0%,
      #f5f6fa 100%
    );
    border-bottom-left-radius: 60rpx;
    border-bottom-right-radius: 60rpx;
  }
}

.user-card {
  position: relative;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 24rpx;
  padding: 40rpx 30rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
  margin: 40rpx 0;
  .user-info {
    display: flex;
    align-items: center;

    .avatar {
      width: 120rpx;
      height: 120rpx;
      border-radius: 60rpx;
      border: 4rpx solid rgba(255, 255, 255, 0.9);
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.15);
      transition: transform 0.3s ease;

      &:active {
        transform: scale(0.95);
      }
    }

    .info-content {
      flex: 1;
      margin: 0 24rpx;

      .nickname {
        font-size: 34rpx;
        font-weight: 600;
        color: #2c3e50;
        margin-bottom: 8rpx;
        display: flex;
        align-items: center;

        &::after {
          content: "";
          display: inline-block;
          width: 12rpx;
          height: 12rpx;
          background: #4cd964;
          border-radius: 50%;
          margin-left: 12rpx;
        }
      }

      .signature {
        font-size: 26rpx;
        color: #7f8c8d;
        line-height: 1.4;
      }
    }

    .edit-btn {
      width: 64rpx;
      height: 64rpx;
      border-radius: 32rpx;
      background: rgba(255, 255, 255, 0.2);
      backdrop-filter: blur(10px);
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s ease;
      border: 1px solid rgba(255, 255, 255, 0.1);

      &:active {
        transform: scale(0.9);
        background: rgba(255, 255, 255, 0.3);
      }
    }
  }
}

.login-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30rpx 0;

  .default-avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 60rpx;
    margin-bottom: 24rpx;
    background: linear-gradient(45deg, #e6e6e6, #f5f5f5);
    border: 4rpx solid rgba(255, 255, 255, 0.9);
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  }

  .login-tip {
    font-size: 28rpx;
    color: #7f8c8d;
    margin-bottom: 32rpx;
    letter-spacing: 1rpx;
    text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.05);
  }

  .wx-login-btn {
    background: linear-gradient(45deg, #07c160, #0baf56);
    border: none;
    border-radius: 40rpx;
    color: #fff;
    font-size: 28rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 50rpx;
    height: 76rpx;
    box-shadow: 0 6rpx 16rpx rgba(7, 193, 96, 0.2);
    transition: all 0.3s ease;

    text {
      margin-left: 12rpx;
      letter-spacing: 1rpx;
      font-weight: 500;
    }

    .u-icon {
      opacity: 0.9;
    }

    &::after {
      border: none;
    }

    &:active {
      transform: scale(0.96);
      box-shadow: 0 4rpx 8rpx rgba(7, 193, 96, 0.15);
    }
  }
}

.feature-list {
  margin: 30rpx;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 24rpx;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.06);
  overflow: hidden;

  .feature-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 28rpx 24rpx;
    border-bottom: 1px solid rgba(0, 0, 0, 0.03);
    transition: all 0.3s ease;

    &:active {
      background-color: rgba(0, 0, 0, 0.02);
    }

    .feature-content {
      display: flex;
      align-items: center;

      .icon-wrapper {
        width: 72rpx;
        height: 72rpx;
        border-radius: 16rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
        margin-right: 20rpx;
        transition: all 0.3s ease;

        .u-icon {
          transform: scale(0.9);
        }
      }

      .feature-text {
        font-size: 28rpx;
        font-weight: 500;
        color: #333;
      }
    }

    .u-icon {
      opacity: 0.5;
      transform: scale(0.9);
    }
  }

  .feedback-btn {
    width: 100%;
    background: transparent;
    line-height: normal;

    &::after {
      border: none;
    }
  }


}

.version-info {
  text-align: center;
  padding: 40rpx;
  font-size: 26rpx;
  color: #909399;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}
</style>
