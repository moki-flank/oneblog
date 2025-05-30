<template>
    <view class="container">
      <!-- 发布说说按钮 -->
      <view class="publish-btn" @click="handlePublish">
        <u-icon name="edit-pen" color="#fff" size="24"></u-icon>
        <text>发布说说</text>
      </view>
  
      <!-- 说说列表 -->
      <view class="moments-list">
        <view class="moment-item" v-for="(item, index) in momentsList" :key="index">
          <!-- 用户信息 -->
          <view class="user-info">
            <image class="avatar" :src="item.avatar" mode="aspectFill"></image>
            <view class="info-content">
              <text class="nickname">{{item.nickname}}</text>
              <text class="time">{{formatTime(item.createTime)}}</text>
            </view>
          </view>
  
          <!-- 说说内容 -->
          <view class="content">
            <text class="text">{{item.content}}</text>
            <!-- 图片展示 -->
            <u-album :urls=" item.images" keyName="src2"></u-album>
          </view>
  
          <!-- 位置信息 -->
          <view class="location" v-if="item.location">
            <u-icon name="map" size="24" color="#909399"></u-icon>
            <text>{{item.location}}</text>
          </view>
  
          <!-- 互动区域 -->
          <view class="interaction">
            <view class="action-btn" @click="handleLike(index)">
              <u-icon :name="item.isLiked ? 'heart-fill' : 'heart'" :color="item.isLiked ? '#ff5733' : '#909399'" size="28"></u-icon>
              <text :class="{ active: item.isLiked }">{{item.likes}}</text>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 加载状态提示 -->
      <u-loadmore :status="loadStatus" bgColor="#f5f6fa"></u-loadmore>
      
      <!-- 空状态提示 -->
      <view class="empty-status" v-if="!loading && momentsList.length === 0">
        <text>暂无数据</text>
      </view>
    </view>
  </template>
  
  <script>
  import { getMomentsApi } from '@/api/moment'
  import { formatTime } from '@/utils/time'
  export default {
    data() {
      return {
        momentsList: [],
        params: {
            pageNum: 1,
            pageSize: 10
        },
        pages: 0,
        loadStatus: 'loadmore',  // 加载状态
        loading: false  // 是否加载完成
      }
    },
    onLoad() {
        this.getMoments()
    },
    // 添加上拉触底事件监听
    onReachBottom() {
        if (this.loadStatus === 'loadmore') {
            this.getMoments()
        }
    },
    methods: {
        /**

         * 格式化时间
         */
         formatTime(time) {
            return formatTime(time)
        },

        // 获取说说列表
        getMoments() {
            if (this.loading) return
            this.loading = true
            this.loadStatus = 'loading'
            
            getMomentsApi(this.params).then(res => {
                // 处理每个moment对象，确保有必要的属性
                const records = res.data.records.map(moment => ({
                    ...moment,
                    images: this.parseImages(moment.images),
                    isLiked: moment.isLiked || false,
                    likes: moment.likes || 0
                }))
                
                // 如果是第一页，直接赋值；否则追加数据
                if (this.params.pageNum === 1) {
                    this.momentsList = records
                } else {
                    this.momentsList = [...this.momentsList, ...records]
                }
                
                this.pages = res.data.pages
                this.loadStatus = this.params.pageNum >= this.pages ? 'nomore' : 'loadmore'
                this.params.pageNum++
            }).finally(() => {
                this.loading = false
            })

        },
        /**
         * 解析图片
         */
        parseImages(images) {
            if (!images) return []
            return images.split(',').filter(img => img)
        },

        /**
         * 发布说说
         */
        handlePublish() {
            this.$u.toast('功能建设中...')
        },

        /**
         * 点赞/取消点赞
         */
        handleLike(index) {
            const moment = this.momentsList[index]
            // 确保likes属性存在初始值

            if (typeof moment.likes === 'undefined') {
                this.$set(moment, 'likes', 0)
            }
            
            // 使用Vue的响应式更新
            this.$set(moment, 'isLiked', !moment.isLiked)
            this.$set(moment, 'likes', moment.likes + (moment.isLiked ? 1 : -1))
            
            // 调用点赞/取消点赞接口
            // uni.request({
            //     url: '/api/moment/like/' + moment.id,
            //     method: 'POST',
            //     success: (res) => {
            //         if (res.statusCode !== 200) {
            //             // 如果请求失败，回滚UI更新
            //             moment.isLiked = !moment.isLiked
            //             moment.likes += moment.isLiked ? 1 : -1
            //             uni.showToast({
            //                 title: '操作失败',
            //                 icon: 'none'
            //             })
            //         }
            //     },
            //     fail: () => {
            //         // 网络请求失败，回滚UI更新
            //         moment.isLiked = !moment.isLiked
            //         moment.likes += moment.isLiked ? 1 : -1
            //         uni.showToast({
            //             title: '网络错误',
            //             icon: 'none'
            //         })
            //     }
            // })
        }
    }
  }
  </script>
  
  <style lang="scss" scoped>
  .container {
    min-height: 100vh;
    background-color: #f5f6fa;
    padding: 20rpx;
  }
  
  .publish-btn {
    position: fixed;
    right: 30rpx;
    bottom: 100rpx;
    background: linear-gradient(45deg, #2979ff, #5e35b1);
    width: 180rpx;
    height: 72rpx;
    border-radius: 36rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 6rpx 16rpx rgba(41, 121, 255, 0.2);
    z-index: 100;
    transition: all 0.3s ease;
    
    text {
      color: #fff;
      font-size: 28rpx;
      margin-left: 8rpx;
    }
    
    &:active {
      transform: scale(0.95);
    }
  }
  
  .moments-list {
    .moment-item {
      background: #fff;
      border-radius: 20rpx;
      padding: 24rpx;
      margin-bottom: 20rpx;
      box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
      
      .user-info {
        display: flex;
        align-items: center;
        margin-bottom: 20rpx;
        
        .avatar {
          width: 80rpx;
          height: 80rpx;
          border-radius: 40rpx;
          margin-right: 16rpx;
        }
        
        .info-content {
          flex: 1;
          
          .nickname {
            font-size: 28rpx;
            font-weight: 500;
            color: #2c3e50;
            margin-bottom: 4rpx;
            margin-right: 10rpx;
          }
          
          .time {
            font-size: 24rpx;
            color: #909399;
          }
        }
      }
      
      .content {
        .text {
          font-size: 28rpx;
          color: #333;
          line-height: 1.6;
          margin-bottom: 16rpx;
        }
      }
      
      .location {
        display: flex;
        align-items: center;
        margin-top: 16rpx;
        
        text {
          font-size: 24rpx;
          color: #909399;
          margin-left: 8rpx;
        }
      }
      
      .interaction {
        display: flex;
        justify-content: flex-end;
        margin-top: 20rpx;
        padding-top: 20rpx;
        border-top: 1px solid #f5f6fa;
        
        .action-btn {
          display: flex;
          align-items: center;
          margin-left: 32rpx;
          
          text {
            font-size: 26rpx;
            color: #909399;
            margin-left: 8rpx;
            
            &.active {
              color: #ff5733;
            }
          }
        }
      }
    }
  }
  
  .loading-status, .empty-status {
    text-align: center;
    padding: 20rpx 0;
    
    text {
        font-size: 26rpx;
        color: #909399;
    }
  }
  </style> 