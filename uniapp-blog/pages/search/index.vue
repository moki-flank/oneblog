<template>
    <view class="search-page">
        <view class="search-box">
            <input v-model="params.keyword" type="text" placeholder="想搜点什么呢"  class="search-input" />
            <button class="search-button" @click="handleSearch">
                搜索
            </button>
        </view>

        <!-- <view class="hot-search">
            <view class="hot-title">热门搜索</view>
            <view class="hot-tags">
                <view v-for="(tag, index) in hotTags" :key="index" class="tag-item" @click="quickSearch(tag)">
                    <text class="tag-prefix">#</text>
                    {{ tag }}
                </view>
            </view>
        </view> -->

        <view class="search-results" v-if="searchResults.length">
            <view class="results-header">
                <text class="results-title">搜索结果</text>
                <!-- <text class="filter-btn">筛选 ▾</text> -->
            </view>
            <view class="results-list">
                <view v-for="(result, index) in searchResults" :key="index" class="result-item"
                    @click="goToDetail(result.id)">
                    <view class="result-title">
                        <rich-text :nodes="highlightKeyword(result.title)"></rich-text>
                    </view>
                    <view class="result-description">
                        <rich-text :nodes="highlightKeyword(result.summary)"></rich-text>
                        <text class="ellipsis">...</text>
                    </view>
                </view>
            </view>
            <view class="load-more" v-if="hasMore" @click="loadMore">
                <text class="load-more-text">点击加载更多</text>
            </view>
        </view>
    </view>
</template>

<script>
import { getArticlesApi } from '@/api/article'

export default {
    data() {
        return {
            searchResults: [],
            hotTags: [],
            hasMore: false,
            params: {
                keyword: '',
                pageNum: 1,
                pageSize: 10,
            },
            pages: 0,
        }
    },

    methods: {
        /**
         * 搜索文章
         */
        async handleSearch() {
            if (!this.params.keyword.trim()) {
                this.searchResults = []
                return
            }

            uni.showLoading({
                title: "搜索中...",
            });
           
            try {
                const res = await getArticlesApi(this.params)

                if (res.code === 200) {
                    if (this.params.pageNum === 1) {
                        this.searchResults = res.data.records
                    } else {

                        this.searchResults = [...this.searchResults, ...res.data.records]
                    }
                    this.pages = res.data.pages
                    this.hasMore = this.params.pageNum < this.pages
                }
                uni.hideLoading()
            } catch (error) {
                console.error('搜索失败:', error)
                uni.hideLoading()
                uni.showToast({
                    title: '搜索失败',
                    icon: 'none'
                })

            }
        },

        /**
         * 根据标签搜索
         * @param tag 
         */
        quickSearch(tag) {
            this.params.keyword = tag
            this.page = 1
            this.handleSearch()
        },

        /**
         * 加载更多
         */
        async loadMore() {
            if (!this.hasMore) return

            this.params.pageNum++
            this.handleSearch()
        },

        goToDetail(id) {
            uni.navigateTo({
                url: `/pages/article/detail?id=${id}`
            })
        },

        /**
         * 高亮关键词
         * @param text 需要处理的文本
         * @returns 处理后的nodes数组
         */
        highlightKeyword(text) {
            if (!text || !this.params.keyword) return [{ type: 'text', text }]
            
            const keyword = this.params.keyword.trim()
            if (!keyword) return [{ type: 'text', text }]

            try {
                // 处理特殊字符
                const escapeRegExp = (string) => {
                    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
                }
                
                const escapedKeyword = escapeRegExp(keyword)
                // 分割文本
                const parts = text.split(new RegExp(`(${escapedKeyword})`, 'gi'))
                
                // 转换为rich-text需要的nodes格式
                return parts.map(part => {
                    if (part.toLowerCase() === keyword.toLowerCase()) {
                        // 关键词部分
                        return {
                            name: 'span',
                            attrs: {
                                style: 'color: red; font-weight: bold;'
                            },
                            children: [{
                                type: 'text',
                                text: part
                            }]
                        }
                    }
                    // 非关键词部分
                    return {
                        type: 'text',
                        text: part
                    }
                })
            } catch (e) {
                console.error('高亮处理失败:', e)
                return [{ type: 'text', text }]
            }
        }
    }
}
</script>

<style scoped>
.search-page {
    min-height: 100vh;
    background: #fff;
    padding: 0 20px;
}

.search-box {
    display: flex;
    align-items: center;
    padding: 15px 0;
    gap: 10px;
}

.search-input {
    flex: 1;
    height: 40px;
    background: #f5f5f5;
    border-radius: 20px;
    padding: 0 20px;
    font-size: 14px;
}

.search-button {
    width: 80px;
    height: 40px;
    background: #409EFF;
    color: #fff;
    border-radius: 20px;
    font-size: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.hot-search {
    margin-top: 20px;
}

.hot-title {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 15px;
}

.hot-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
}

.tag-item {
    background: #f5f5f5;
    padding: 8px 15px;
    border-radius: 16px;
    font-size: 14px;
    color: #333;
}

.tag-prefix {
    color: #999;
    margin-right: 4px;
}

.search-results {
    margin-top: 20px;
}

.results-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
}

.results-title {
    font-size: 16px;
    font-weight: bold;
}

.filter-btn {
    color: #666;
    font-size: 14px;
}

.result-item {
    padding: 15px 0;
    border-bottom: 1px solid #eee;
}

.result-title {
    font-size: 16px;
    color: #333;
    font-weight: bold;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    margin-bottom: 8px;
    line-height: 1.4;
    position: relative;
}

.result-description {
    font-size: 14px;
    color: #666;
    line-height: 1.5;
    height: 3em;
    overflow: hidden;
    margin-bottom: 8px;
    position: relative;
}

.ellipsis {
    position: absolute;
    right: 0;
    bottom: 0;
    padding-left: 40px;
    background: linear-gradient(to right, rgba(255, 255, 255, 0), #fff 40%);
}

.result-item:active {
    background-color: #f9f9f9;
}

.load-more {
    text-align: center;
    padding: 20px 0;
}

.load-more-text {
    color: #999;
    font-size: 14px;
}

.load-more:active {
    opacity: 0.7;
}
</style>
