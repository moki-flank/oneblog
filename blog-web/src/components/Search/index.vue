<template>
  <el-dialog
    :visible.sync="$store.state.searchVisible"
    title="搜索"
    width="650px"
    @update:visible="handleClose"
  >
    <div 
      class="search-input-wrapper"
      :class="{ loading }"
    >
      <i class="fas fa-search search-icon"></i>
      <input
        ref="searchInput"
        v-model="params.keyword"
        type="text"
        class="search-input"
        placeholder="输入关键词搜索文章..."
        @keyup.enter="handleSearch"
      />
      <div v-if="params.keyword" class="clear-btn" @click="clearSearch">
        <i class="fas fa-times"></i>
      </div>
      <span class="enter-tip">
        <i class="fas fa-level-down-alt fa-rotate-90"></i>
        按回车搜索
      </span>
    </div>

    <div class="hot-searches" v-if="searchResults.length === 0">
      <h4>
        <i class="fas fa-fire"></i>
        标签搜索
      </h4>
      <div class="hot-tags">
        <router-link 
          v-for="tag in tags" 
          :key="tag.id"
          :to="`/tags?tagId=${tag.id}&tagName=${tag.name}`"
          class="hot-tag"
        >
          {{ tag.name }}
        </router-link>
      </div>
    </div>
    
    <div v-if="searchResults.length > 0" class="search-results" v-loading="loading">
      <div
        v-for="item in searchResults"
        :key="item.id"
        class="search-result-item"
        @click="handleResultClick(item)"
      >
        <div class="result-header">
          <h3 v-html="highlightKeyword(item.title)"></h3>
          <span class="result-date">{{ formatDate(item.createTime) }}</span>
        </div>
        <p v-html="highlightKeyword(item.summary)"></p>
        <div class="result-footer">
          <span class="result-category">
            <i class="fas fa-folder"></i>
            {{ item.categoryName }}
          </span>
          <span class="result-views">
            <i class="fas fa-eye"></i>
            {{ item.quantity }} 阅读
          </span>
        </div>
      </div>

    
      <div class="pagination-box">
        <el-pagination background v-if="total" @current-change="handlePageChange" :current-page="params.pageNum"
          :page-size="params.pageSize" layout="prev, pager, next" :total="total">
        </el-pagination>
      </div>
    </div>

    <el-empty
      v-if="searchResults.length === 0"
      description="输入关键词搜索文章.."
    />
  </el-dialog>
</template>

<script>
import { getArticlesApi } from '@/api/article'
import { getTagsApi } from '@/api/tags'
export default {
  name: 'SearchDialog',
  
  data() {
    return {
      searchResults: [],
      loading: false,
      searchTimeout: null,
      tags: [],
      params: {
        keyword: '',
        pageNum: 1,
        pageSize: 10
      },
      hasSearched: false,
      total: 0,
    }
  },
  watch: {
    '$store.state.searchVisible': {
      handler(newVal, oldVal) {
        if (newVal) {
          getTagsApi().then(res => {
            this.tags = res.data
          })
        }
      }
    }
  },  
  methods: {
    /**
     * 处理分页
     */
    handlePageChange(page) {
      this.params.pageNum = page
      this.handleSearch()
    },
    /**
     * 搜索
     */
    async handleSearch() {
      
        if (!this.params.keyword.trim()) {
          this.searchResults = []
          this.hasSearched = false
          return
        }

        this.loading = true
        try {
          const res = await getArticlesApi(this.params)
          this.total = res.data.total
          this.searchResults = res.data.records.map(item => ({
            ...item,
            title: this.truncateText(item.title, 50),
            summary: this.truncateText(item.summary || item.content, 150)
          }))
          this.hasSearched = true
        } catch (error) {
          this.$message.error(error.message)
        } finally {
          this.loading = false
        }
    },
    /**
     * 截断文本
     */
    truncateText(text, maxLength) {
      if (!text) return ''
      return text.length > maxLength 
        ? text.substring(0, maxLength) + '...'
        : text
    },
    /**
     * 高亮关键词
     */
    highlightKeyword(text) {
      if (!this.params.keyword || !text) return text
      const keywords = this.params.keyword.split(/\s+/).filter(k => k)
      let highlightedText = text
      
      keywords.forEach(keyword => {
        const regex = new RegExp(`(${this.escapeRegExp(keyword)})`, 'gi')
        highlightedText = highlightedText.replace(regex, '<mark>$1</mark>')
      })
      
      return highlightedText
    },
    /**
     * 转义正则表达式
     */
    escapeRegExp(string) {
      return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    },
    /**
     * 处理搜索结果点击
     */
    handleResultClick(item) {
      this.$router.push(`/post/${item.id}`)
      this.close()
    },

    /**
     * 关闭搜索对话框
     */
    close() {
      this.$store.commit('SET_SEARCH_VISIBLE', false)
      this.params.keyword = ''
      this.searchResults = []
      this.hasSearched = false
    },
    /**
     * 处理搜索对话框关闭
     */
    handleClose() {
      this.close()
    },
    /**
     * 处理键盘事件
     */
    handleKeydown(e) {
      if (e.key === 'Escape') {
        this.close()
      }
    },
    /**
     * 清除搜索
     */
    clearSearch() {
      this.params.keyword = ''
      this.searchResults = []
      this.hasSearched = false
    },
    /**
     * 格式化日期
     */
    formatDate(date) {
      return new Date(date).toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }
  },

  mounted() {
    document.addEventListener('keydown', this.handleKeydown)
  },

  beforeDestroy() {
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout)
    }
    document.removeEventListener('keydown', this.handleKeydown)
  }
}
</script>

<style lang="scss" scoped>
.search-input-wrapper {
  display: flex;
  align-items: center;
  width: 100%;
  border: 2px solid var(--border-color);
  border-radius: 8px;
  padding: 12px 15px;
  margin-bottom: 20px;
  position: relative;
  transition: all 0.3s ease;
  
  &:focus-within {
    border-color: $primary;
    box-shadow: 0 0 0 3px rgba(202, 90, 210, 0.1);
  }
  
  .search-icon {
    color: #909399;
    font-size: 18px;
    margin-right: 10px;
  }
  
  .clear-btn {
    cursor: pointer;
    padding: 4px;
    color: #909399;
    transition: all 0.3s ease;
    
    &:hover {
      color: #f56c6c;
    }
  }
  
  &.loading::after {
    content: '';
    position: absolute;
    right: 15px;
    width: 20px;
    height: 20px;
    border: 2px solid #dcdfe6;
    border-top-color: var(--primary-color);
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }
  
  .enter-tip {
    position: absolute;
    right: 15px;
    color: #909399;
    font-size: 12px;
    display: flex;
    align-items: center;
    opacity: 0.6;
    pointer-events: none;
    
    i {
      font-size: 14px;
      margin-right: 4px;
    }
  }
  
  .clear-btn + .enter-tip {
    right: 40px;
  }
  
  &.loading .enter-tip {
    display: none;
  }
  
  &:focus-within .enter-tip {
    opacity: 0;
    transform: translateX(10px);
    transition: all 0.3s ease;
  }
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 16px;
  padding: 5px;
  width: 100%;
  background: transparent;
  color: $text-secondary;
}


.hot-searches {
  margin-top: 30px;
  
  h4 {
    margin: 0 0 15px;
    color: #606266;
    font-size: 16px;
    display: flex;
    align-items: center;
    
    i {
      color: #ff9800;
      margin-right: 8px;
    }
  }
}

.search-results {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 10px;
  margin-top: $spacing-md;
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-thumb {
    background-color: #e4e7ed;
    border-radius: 3px;
    
    &:hover {
      background-color: #c0c4cc;
    }
  }
}

.search-result-item {
  padding: 15px;
  cursor: pointer;
  border-radius: 8px;
  margin-bottom: 10px;
  transition: all 0.3s ease;
  border: 1px solid var(--border-color);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border-color: var(--primary-color);
  }
  
  .result-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    
    h3 {
      margin: 0;
      font-size: 16px;
      color: $primary;
      flex: 1;
    }
    
    .result-date {
      font-size: 12px;
      color: #909399;
      margin-left: 10px;
    }
  }
  
  p {
    margin: 0 0 10px;
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
  }
  
  .result-footer {
    display: flex;
    gap: 15px;
    font-size: 12px;
    color: #909399;
    
    i {
      margin-right: 4px;
    }
    
    .result-category,
    .result-views {
      display: flex;
      align-items: center;
    }
  }
}


.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hot-tag {
  padding: 6px 12px;
  background-color: #f4f4f5;
  border-radius: 20px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  &:hover {
    color: #fff;
    background-color: $primary;
    transform: translateY(-2px);
  }
}

:deep(mark) {
  background-color: rgba(202, 90, 210, 0.2);
  padding: 0 2px;
  border-radius: 2px;
  color: var(--primary-color);
  font-weight: 500;
  transition: all 0.3s ease;
  
  &:hover {
    background-color: rgba(202, 90, 210, 0.4);
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style> 