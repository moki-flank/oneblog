<template>
    <div class="hot-search-container">
      <div class="hot-search">
        <!-- 搜索框部分 -->
        <el-card class="search-box">
          <div class="search-input">
            <input 
              v-model="searchText" 
              :placeholder="`在 ${getCurrentEngine.label} 中搜索...`"
              @keyup.enter="handleSearch"
            >
            <button class="search-btn" @click="handleSearch">
              <i class="fas fa-search"></i>
            </button>
          </div>
          <div class="search-engines">
            <button 
              v-for="engine in searchEngines" 
              :key="engine.name"
              :class="['engine-btn', { active: currentEngine === engine.name }]"
              @click="currentEngine = engine.name"
            >
              <svg-icon :icon-class="engine.icon"></svg-icon>
              {{ engine.label }}
            </button>
          </div>
        </el-card>
  
        <!-- 热搜列表部分 -->
        <el-card class="hot-lists">
          <div class="hot-tabs">
            <button 
              v-for="tab in hotTabs" 
              :key="tab.type"
              :class="['tab-btn', { active: currentTab === tab.type }]"
              @click="currentTab = tab.type"
            >
              <i :class="tab.icon"></i>
              {{ tab.label }}
            </button>
          </div>
          
          <div class="hot-content">
            <div class="hot-grid" v-loading="loading">
              <div 
                v-for="(item, index) in currentHotList" 
                :key="index" 
                class="hot-card"
                :class="[currentTab, { 'top-rank': index < 3 }]"
                @click="handleHotItemClick(item)"
              >
                <span class="rank" :class="getRankClass(index)">{{ index + 1 }}</span>
                <div class="card-content">
                  <div class="title-wrap">
                    <a :href="item.url" target="_blank" class="title">{{ item.keyword }}</a>
                    <span class="hot-tag" v-if="item.tag">{{ item.tag }}</span>
                  </div>
                  <div class="card-footer">
                    <span class="hot-value" v-if="item.hotValue">
                      <i class="fas fa-fire"></i>
                      {{ formatHotValue(item.hotValue) }}
                    </span>
                    <span class="trend" v-if="item.trend">
                      <i :class="getTrendIcon(item.trend)"></i>
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </template>
  
  <script>
  import { getHotListApi } from '@/api/hot-search'
  export default {
    name: 'HotSearch',
    data() {
      return {
        searchText: '',
        currentEngine: 'baidu',
        currentTab: 'weibo',
        loading: false,
        searchEngines: [
          { name: 'baidu', label: '百度', icon: 'baidu', url: 'https://www.baidu.com/s?wd=' },
          { name: 'google', label: '谷歌', icon: 'chrome', url: 'https://www.google.com/search?q=' },
          { name: 'gitee', label: '码云', icon: 'gitee', url: 'https://search.gitee.com/?q=' },
          { name: 'github', label: 'GitHub', icon: 'github', url: 'https://github.com/search?q=' }
        ],
        hotTabs: [
          { type: 'weibo', label: '微博热搜', icon: 'fab fa-weibo' },
          { type: 'zhihu', label: '知乎热榜', icon: 'fab fa-zhihu' },
          { type: 'toutiao', label: '头条热榜', icon: 'fas fa-fire' },
          { type: 'baidu', label: '百度热搜', icon: 'fas fa-chart-line' },
          { type: 'csdn', label: 'CSDN热榜', icon: 'fas fa-code' }
        ],
        hotLists: {
          weibo: [],
          zhihu: [],
          toutiao: [],
          baidu: [],
          csdn: []
        }
      }
    },
    computed: {
      getCurrentEngine() {
        return this.searchEngines.find(engine => engine.name === this.currentEngine)
      },
      currentHotList() {
        return this.hotLists[this.currentTab] || []
      }
    },
    methods: {
      handleSearch() {
        if (!this.searchText.trim()){
          this.$message.warning('请输入搜索内容')
          return
        }
        const engine = this.getCurrentEngine
        window.open(engine.url + encodeURIComponent(this.searchText), '_blank')
      },
      getRankClass(index) {
        if (index < 3) return `rank-${index + 1}`
        return ''
      },
      formatHotValue(value) {
        if (value >= 10000) {
          return (value / 10000).toFixed(1) + 'w'
        }
        return value
      },
      async fetchHotList(type) {
        this.loading = true
        try {
          // 这里替换为实际的API调用
          const res = await getHotListApi(type)
          this.hotLists[type] = res.data.data
        } catch (error) {
          this.$message.error('获取热搜数据失败')
        } finally {
          this.loading = false
        }
      },
      handleHotItemClick(item) {
        if (item.url) {
          window.open(item.url, '_blank')
        }
      },
      getTrendIcon(trend) {
        if (trend === '上升') return 'fas fa-arrow-up text-success'
        if (trend === '下降') return 'fas fa-arrow-down text-danger'
        return 'fas fa-minus text-warning'
      }
    },
    watch: {
      currentTab: {
        immediate: true,
        handler(newTab) {
          this.fetchHotList(newTab)
        }
      }
    }
  }
  </script>
  
  <style lang="scss" scoped>
  .hot-search-container {
    padding: $spacing-lg;
  }
  
  .hot-search {
    max-width: 1200px;
    margin: 0 auto;
  }
  
  
  .search-box {
    margin-bottom: $spacing-xl;
    padding: $spacing-lg;
  
    .search-input {
      position: relative;
      margin-bottom: $spacing-lg;
      
      input {
        width: 100%;
        padding: $spacing-md $spacing-md;
        padding-right: 60px;
        border-radius: $border-radius-md;
        border-color: var(--border-color);
        font-size: 1.1em;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        background-color: var(--card-bg);
        &:focus {
          outline: none;
          border-color: $primary;
          box-shadow: 0 0 0 4px rgba($primary, 0.1);
        }
      }
  
      .search-btn {
        position: absolute;
        right: $spacing-md;
        top: 50%;
        transform: translateY(-50%);
        background: $primary;
        border: none;
        color: var(--card-bg);
        width: 40px;
        height: 40px;
        border-radius: 50%;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
        &:hover {
          transform: translateY(-50%) scale(1.1);
          box-shadow: 0 4px 12px rgba($primary, 0.3);
        }
      }
    }
  
    .search-engines {
      display: flex;
      flex-wrap: wrap;
      gap: $spacing-sm;
  
      .engine-btn {
        padding: $spacing-sm $spacing-lg;
        border: none;
        background: var(--hover-bg);
        color: var(--text-secondary);
        border-radius: $border-radius-md;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        display: flex;
        align-items: center;
        gap: $spacing-sm;
        font-size: 0.95em;
        font-weight: 500;
  
        &.active {
          background: $primary;
          color: var(--text-primary);
          transform: translateY(-1px);
          box-shadow: 0 4px 12px rgba($primary, 0.2);
        }
  
        &:hover:not(.active) {
          color: $primary;
          background: var(--hover-bg);
          transform: translateY(-1px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        }
  
        svg-icon {
          font-size: 1.2em;
        }
      }
    }
  }
  
  .hot-lists {
    padding: $spacing-lg;
  
    .hot-tabs {
      display: flex;
      gap: $spacing-md;
      margin-bottom: $spacing-xl;
      padding: 0 $spacing-md;
      border-bottom: 1px solid rgba(255, 255, 255, 0.3);
      overflow-x: auto;
      scrollbar-width: none;
      
      &::-webkit-scrollbar {
        display: none;
      }
  
      .tab-btn {
        padding: $spacing-md $spacing-xl;
        border: none;
        background: none;
        color: var(--text-secondary);
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        display: flex;
        align-items: center;
        gap: $spacing-sm;
        white-space: nowrap;
        position: relative;
        font-size: 1em;
        font-weight: 500;
  
        &::after {
          content: '';
          position: absolute;
          bottom: -1px;
          left: 50%;
          width: 0;
          height: 3px;
          background: $primary;
          border-radius: 3px;
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }
  
        &.active {
          color: $primary;
  
          &::after {
            width: 100%;
            left: 0;
          }
        }
  
        &:hover:not(.active) {
          color: $primary;
        }
  
        i {
          font-size: 1.2em;
        }
      }
    }
  
    .hot-content {
      padding: 0 $spacing-md;
  
      .hot-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
        gap: $spacing-lg;
  
        .hot-card {
          position: relative;
          border: 1px solid var(--border-color);
          border-radius: $border-radius-md;
          padding: $spacing-md;
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          display: flex;
          gap: $spacing-md;
          cursor: pointer;
          
          &:hover {
            transform: translateY(-3px);
            background: var(--hover-bg);
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  
            .title {
              color: $primary;
            }
  
            .rank {
              transform: scale(1.1);
            }
          }
  
   
  
          .rank {
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 10px;
            font-size: 1.1em;
            font-weight: 600;
            color: var(--text-primary);
            flex-shrink: 0;
            transition: all 0.3s ease;
  
            &.rank-1 { background: linear-gradient(135deg, #ff6b6b, #ee5253); }
            &.rank-2 { background: linear-gradient(135deg, #5c7cfa, #4263eb); }
            &.rank-3 { background: linear-gradient(135deg, #ffd43b, #fab005); }
          }
  
          .card-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            gap: $spacing-sm;
  
            .title-wrap {
              display: flex;
              align-items: flex-start;
              gap: $spacing-sm;
            }
  
            .title {
              color: var(--text-primary);
              text-decoration: none;
              font-size: 1.1em;
              line-height: 1.5;
              font-weight: 500;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
              transition: color 0.3s ease;
              flex: 1;
  
              &:hover {
                text-decoration: none;
              }
            }
  
            .hot-tag {
              padding: 3px 8px;
              border-radius: 6px;
              background: rgba($primary, 0.1);
              color: $primary;
              font-size: 0.85em;
              font-weight: 500;
              white-space: nowrap;
            }
  
            .card-footer {
              display: flex;
              align-items: center;
              justify-content: space-between;
              margin-top: auto;
              font-size: 0.9em;
  
              .hot-value {
                display: flex;
                align-items: center;
                gap: 6px;
                color: #ff6b6b;
                font-weight: 500;
                
                i {
                  font-size: 1.1em;
                }
              }
  
              .trend {
                i {
                  font-size: 1.1em;
                  &.text-success { color: #2ecc71; }
                  &.text-danger { color: #e74c3c; }
                  &.text-warning { color: #f1c40f; }
                }
              }
            }
          }
        }
      }
    }
  }
  
  @include responsive(sm) {
    .hot-search-container {
      padding: $spacing-sm;
    }
  
    .hot-search {
      .search-box,
      .hot-lists {
        padding: $spacing-md;
      }
  
      .hot-lists {
        .hot-content {
          .hot-grid {
            grid-template-columns: 1fr;
            gap: $spacing-md;
          }
        }
      }
    }
  }
  </style> 