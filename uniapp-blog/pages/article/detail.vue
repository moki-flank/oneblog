<template>
  <view class="container">
    <!-- 导航栏 -->
    <nav-bar 
      :title="article.title"
      background="linear-gradient(to bottom, rgba(0,0,0,0.7), rgba(0,0,0,0))"
      showHome
    />
    
    <!-- 文章头部 -->
    <view class="article-header">
      <image :src="article.cover" mode="aspectFill" class="cover-image"></image>
      <view class="header-content">
        <view class="category-tag">{{article.category.name}}</view>
        <text class="title">{{article.title}}</text>
        <view class="meta">
          <text class="date">{{article.createTime}}</text>


          <text class="views">{{article.quantity}}阅读</text>
        </view>
      </view>
    </view>

    <!-- 文章内容 -->
    <view class="article-content">
      <view class="author-info">
        <image class="avatar" :src="article.avatar" mode="aspectFill"></image>
        <view class="info">
          <text class="name">{{article.nickname}}</text>
          <text class="desc">{{article.author.desc}}</text>
        </view>
      </view>
      <!-- Markdown 内容 -->
      <view class="markdown-body">
        <view class="markdown-content">
          <!-- <rich-text :nodes="markdownHtml"></rich-text> -->
          <mp-html :content="article.content" />
        </view>
      </view>
      
      <!-- 文章标签 -->
      <view class="article-tags">
        <text class="tag" v-for="(tag, index) in article.tags" :key="index">#{{tag.name}}</text>
      </view>
    </view>

    <!-- 底部工具栏 -->
    <view class="bottom-toolbar">
      <view class="toolbar-left">
        <view class="tool-item" @click="handleLike">
          <u-icon :name="isLiked ? 'heart-fill' : 'heart'" :color="isLiked ? '#ff5722' : '#909399'" size="40"></u-icon>
          <text :class="['tool-text', isLiked ? 'active' : '']">点赞</text>
        </view>
        <view class="tool-item" @click="handleCollect">
          <u-icon :name="isCollected ? 'star-fill' : 'star'" :color="isCollected ? '#ffa726' : '#909399'" size="40"></u-icon>
          <text :class="['tool-text', isCollected ? 'active' : '']">收藏</text>
        </view>
      </view>
      <view class="share-btn" @click="handleShare">
        分享文章
      </view>
    </view>
  </view>
</template>

<script>
import { getArticleDetailApi, likeArticleApi, collectArticleApi } from '@/api/article'
import { highlight } from '@/utils/highlight'
import mpHtml from 'mp-html/dist/uni-app/components/mp-html/mp-html'
export default {
  components: {
    navBar: () => import('@/components/nav-bar/index.vue'),
    mpHtml
  },
  data() {
    return {
      article: {},
      isLiked: false,
      isCollected: false,
      markdownHtml: '',
      imageList: []
    }
  },

  onLoad(options) {
    if (options.id) {
      this.getArticleDetail(options.id)
    }
  },
  methods: {
    async getArticleDetail(id) {
      try {
        const res = await getArticleDetailApi(id)
        this.article = res.data
        this.isLiked = res.data.isLiked
        this.isCollected = res.data.isCollected
        // 解析 Markdown 内容
      } catch (error) {
        console.error('获取文章详情失败:', error)
        uni.showToast({
          title: '获取文章详情失败',
          icon: 'none'
        })
      }
    },

    async handleLike() {
      try { 
        await likeArticleApi(this.article.id)
        this.isLiked = !this.isLiked
        uni.showToast({
          title: this.isLiked ? '已点赞' : '已取消点赞',
          icon: 'none'
        })
      } catch (error) {
        uni.showToast({
          title: '操作失败',
          icon: 'none'
        })
      }
    },
    async handleCollect() {
      try {
        await collectArticleApi(this.article.id)
        this.isCollected = !this.isCollected
        uni.showToast({
          title: this.isCollected ? '已收藏' : '已取消收藏',
          icon: 'none'
        })
      } catch (error) {
        uni.showToast({
          title: '操作失败',
          icon: 'none'
        })
      }
    },
    handleShare() {
      uni.showShareMenu({
        withShareTicket: true,
        menus: ['shareAppMessage', 'shareTimeline']
      })
    },

  }
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #fff;
  padding-bottom: 120rpx;
}

.article-header {
  position: relative;
  margin-top: calc(var(--status-bar-height) * -1);

  .cover-image {
    width: 100%;
    height: 400rpx;
  }

  .header-content {
    padding: 30rpx;
    background: linear-gradient(to bottom, rgba(0,0,0,0), rgba(0,0,0,0.7));
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    
    .category-tag {
      display: inline-block;
      font-size: 24rpx;
      color: #fff;
      background-color: rgba(41, 121, 255, 0.8);
      padding: 6rpx 20rpx;
      border-radius: 4rpx;
      margin-bottom: 16rpx;
    }

    .title {
      font-size: 36rpx;
      font-weight: 600;
      color: #fff;
      margin-bottom: 16rpx;
      text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.2);
    }

    .meta {
      font-size: 24rpx;
      color: rgba(255,255,255,0.9);
      display: flex;
      align-items: center;
      
      .views {
        margin-left: 20rpx;
        &::before {
          content: "•";
          margin-right: 20rpx;
        }
      }
    }
  }
}

.article-content {
  padding: 30rpx;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
  
  .author-info {
    display: flex;
    align-items: center;
    margin-bottom: 30rpx;
    padding: 20rpx;
    background-color: #f5f6fa;
    border-radius: 12rpx;
    box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05);
    
    .avatar {
      width: 80rpx;
      height: 80rpx;
      border-radius: 40rpx;
      margin-right: 20rpx;
      border: 2rpx solid #fff;
    }
    
    .info {
      flex: 1;
      
      .name {
        font-size: 28rpx;
        font-weight: 600;
        color: #303133;
        margin-bottom: 6rpx;
      }
      
      .desc {
        font-size: 24rpx;
        color: #606266;
      }
    }
  }
  
  .markdown-body {
    padding: 20rpx 0;
    

  }
  
  .article-tags {
    margin-top: 40rpx;
    display: flex;
    flex-wrap: wrap;
    gap: 20rpx;
    
    .tag {
      font-size: 24rpx;
      color: #2979ff;
      background-color: rgba(41, 121, 255, 0.1);
      padding: 8rpx 24rpx;
      border-radius: 40rpx;
    }
  }
}

.bottom-toolbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 100rpx;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30rpx;
  padding-bottom: env(safe-area-inset-bottom);
  box-shadow: 0 -2rpx 12rpx rgba(0,0,0,0.05);

  .toolbar-left {
    display: flex;
    align-items: center;

    .tool-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-right: 40rpx;
      padding: 10rpx 0;

      .tool-text {
        font-size: 24rpx;
        color: #909399;
        margin-top: 4rpx;

        &.active {
          color: #ff5722;

          &:last-child {
            color: #ffa726;
          }
        }
      }
    }
  }

  .share-btn {
    background-color: #2979ff;
    color: #fff;
    font-size: 28rpx;
    padding: 16rpx 40rpx;
    border-radius: 40rpx;
    box-shadow: 0 4rpx 12rpx rgba(41,121,255,0.2);

    &:active {
      transform: scale(0.98);
    }
  }
}

/deep/ ._root  {
  overflow-x: unset !important;
  overflow-y: unset !important;
}
</style> 