<template>
    <view class="container">
        <!-- 搜索框部分 -->
        <view class="search-section">
            <view class="search-box" @click="navigateToSearch">
                <u-icon name="search" size="20" color="#909399"></u-icon>
                <text class="placeholder">搜索文章</text>
            </view>
        </view>

        <!-- 轮播图部分 -->
        <view class="swiper-section">
            <u-swiper :list="swiperList" keyName="image" showTitle circular :height="200" borderRadius="12"></u-swiper>
        </view>

        <view class="notice-section">
            <u-notice-bar :text="notice.content"></u-notice-bar>
        </view>


        <!-- 分类标签 -->
        <view class="category-section">
            <u-tabs :list="categoryList" @click="onTabChange">
                <view slot="right" style="padding-left: 4px;margin-right: 10px;">
                    <u-icon name="list" size="21" bold></u-icon>
                </view>
            </u-tabs>
        </view>


        <!-- 文章列表 -->
        <view class="article-list">
            <view v-for="(item, index) in articleList" :key="index" class="article-item" @click="navigateToDetail(item)"
                :style="{ 'animation-delay': index * 0.1 + 's' }">
                <view class="article-main">
                    <view class="article-content">
                        <text class="article-title u-line-2">{{ item.title }}</text>
                        <text class="article-desc u-line-2">{{ item.summary }}</text>
                        <view class="article-meta">
                            <view class="meta-left">
                                <text class="category text-ellipsis">{{ item.categoryName }}</text>
                                <text class="date text-ellipsis">{{ formatTime(item.createTime) }}</text>
                            </view>
                            <view class="read-btn">
                                阅读全文
                                <u-icon name="arrow-right" size="12" color="#2979ff"></u-icon>
                            </view>
                        </view>
                    </view>
                    <image :src="item.cover" mode="aspectFill" class="article-image"></image>
                </view>
            </view>
        </view>

        <!-- 加载更多 -->
        <u-loadmore :status="loadStatus" bgColor="#f5f6fa"></u-loadmore>
    </view>
</template>

<script>
import { getArticlesApi, getCarouselArticlesApi, getCategoriesApi } from '@/api/article'
import { getNoticeApi } from '@/api/notice'

import { formatTime } from '@/utils/time'
export default {
    name: 'Home',
    data() {
        return {
            currentTab: 0,
            categoryList: [],
            swiperList: [],
            articleList: [],
            loadStatus: 'loadmore',
            isLoading: false,
            params: {
                pageNum: 1,
                pageSize: 10,
                category: '',
                isRecommend: true
            },
            pages: 0,
            notice:{}
        }
    },
    onLoad() {
        this.getCarouselList()
        this.getArticleList()
        this.getCategories()
		this.getNotice()
    },
    onReachBottom() {
        if (this.loadStatus === 'loadmore') {
            this.getArticleList()
        }
    },
    onPullDownRefresh() {
        this.params.page = 1
        this.articleList = []
        this.loadStatus = 'loadmore'
        Promise.all([
            this.getCarouselList(),
            this.getArticleList()
        ]).then(() => {
            uni.stopPullDownRefresh()
        })
    },
    methods: {
		/**
		 * 获取公告
		 */
		async getNotice() {
			const notice = uni.getStorageSync("notice")
			if(notice) {
				this.notice = notice;
				return;
			}
		    const res = await getNoticeApi()
		    if(res.data && res.data.top) {
				this.notice = res.data.top[0]
				uni.setStorageSync("notice",this.notice)
			}
		},
        /**
         * 格式化时间
         */
        formatTime(time) {
            return formatTime(time)
        },
        /**
         * 获取分类列表
         */

        async getCategories() {
            const res = await getCategoriesApi()
            this.categoryList = [{ id: null, name: '全部' }, ...res.data]
        },
        /**
         * 切换标签
         */
        onTabChange(item) {
            this.currentTab = item
            this.params.pageNum = 1
            console.log(item.id)
            if (item.id == undefined) {
                delete this.params.categoryId
            } else {
                this.params.categoryId = item.id
            }
            this.articleList = []
            this.loadStatus = 'loadmore'
            this.getArticleList()
        },
        /**
         * 获取轮播图列表
         */
        async getCarouselList() {
            try {
                const res = await getCarouselArticlesApi()
                this.swiperList = res.data.map(item => ({
                    image: item.cover,
                    title: item.title,
                    id: item.id
                }))
            } catch (error) {
                console.error('获取轮播图失败:', error)
                uni.showToast({
                    title: '获取轮播图失败',
                    icon: 'none'
                })
            }
        },
        /**
         * 获取文章列表
         */
        async getArticleList() {
            if (this.isLoading) return
            this.isLoading = true
            this.loadStatus = 'loading'

            try {
                const res = await getArticlesApi(this.params)
                if (this.params.pageNum === 1) {
                    this.articleList = res.data.records
                } else {
                    this.articleList = [...this.articleList, ...res.data.records]
                }
                this.pages = res.data.pages
                this.loadStatus = this.params.pageNum >= this.pages ? 'nomore' : 'loadmore'
                this.params.pageNum++
            } catch (error) {
                this.loadStatus = 'loadmore'
                uni.showToast({
                    title: '获取文章列表失败',
                    icon: 'none'
                })
            } finally {
                this.isLoading = false
            }
        },
        /**
         * 跳转到文章详情页
         */
        navigateToDetail(item) {
            uni.navigateTo({
                url: `/pages/article/detail?id=${item.id}`
            })
        },
        /**
         * 跳转到搜索页
         */
        navigateToSearch() {
            uni.navigateTo({
                url: '/pages/search/index'
            })
        },
    }
}
</script>

<style lang="scss" scoped>
.container {
    min-height: 100vh;
    background-color: #f5f6fa;
    padding-bottom: 30rpx;
}

.search-section {
    padding: 20rpx;
    background-color: #fff;
    
    .search-box {
        display: flex;
        align-items: center;
        background-color: #f5f6fa;
        height: 72rpx;
        border-radius: 36rpx;
        padding: 0 30rpx;

        .placeholder {
            margin-left: 16rpx;
            font-size: 28rpx;
            color: #909399;
        }
    }
}

.swiper-section {
    margin-bottom: 10rpx;
}

.notice-section{
    margin: 10rpx 0;
}

.category-section {
    background-color: #fff;
    position: sticky;
    top: 0;
    z-index: 1;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.article-list {

    .article-item {
        margin-bottom: 20rpx;
        animation: fadeInUp 0.5s ease-out forwards;
        opacity: 0;
        transform: translateY(20rpx);

        .article-main {
            background-color: #fff;
            border-radius: 12rpx;
            overflow: hidden;
            box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
            display: flex;
            padding: 24rpx;

            &:active {
                transform: scale(0.98);
                transition: transform 0.2s;
            }

            .article-content {
                flex: 1;
                margin-right: 24rpx;
                display: flex;
                flex-direction: column;

                .article-title {
                    font-size: 32rpx;
                    font-weight: 600;
                    color: #303133;
                    margin-bottom: 16rpx;
                    line-height: 1.4;
                }

                .article-desc {
                    font-size: 28rpx;
                    color: #606266;
                    margin-bottom: 20rpx;
                    line-height: 1.5;
                    flex: 1;
                }

                .article-meta {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;

                    .meta-left {
                        display: flex;
                        align-items: center;
                        flex: 1;
                        min-width: 0;
                        margin-right: 20rpx;

                        .category {
                            font-size: 24rpx;
                            color: #2979ff;
                            background-color: rgba(41, 121, 255, 0.1);
                            padding: 4rpx 12rpx;
                            border-radius: 4rpx;
                            margin-right: 16rpx;
                            max-width: 160rpx;
                            white-space: nowrap;
                            overflow: hidden;
                            text-overflow: ellipsis;
                        }

                        .date {
                            font-size: 24rpx;
                            color: #909399;

                        }
                    }

                    .read-btn {
                        display: flex;
                        align-items: center;
                        font-size: 24rpx;
                        color: #2979ff;
                        flex-shrink: 0;
                    }
                }
            }

            .article-image {
                width: 200rpx;
                height: 200rpx;
                border-radius: 8rpx;
                flex-shrink: 0;
            }
        }
    }
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(20rpx);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.text-ellipsis {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
</style>