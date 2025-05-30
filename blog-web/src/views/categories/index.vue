<template>
    <div class="categories-page">
      <div class="content-layout">
        <main class="main-content">
          <div class="content-card">
            <div class="categories-nav">
              <div 
                v-for="category in categories"
                :key="category.name"
                class="category-tab"
                :class="{ active: activeCategory === category.name }"
                @click="scrollToCategory(category.name)"
              >
                <i class="fas fa-folder-open"></i>
                <span>{{ category.name }}</span>
              </div>
            </div>
  
            <div class="categories-list">
              <div 
                v-for="category in categories" 
                :key="category.name"
                class="category-group"
                v-animate-on-scroll
                :ref="category.name"
              >
                <div class="category-header">
                  <h2 class="category-name">
                    <i class="fas fa-folder-open"></i>
                    {{ category.name }}
                    <span class="post-count">{{ category.posts.length }} 篇文章</span>
                  </h2>
                </div>
                <div class="posts-list">
                  <div 
                    v-for="post in category.posts" 
                    :key="post.id"
                    class="post-item"
                    @click="goToPost(post.id)"
                  >
                    <div class="post-date">
                      <span class="month">{{ formatMonth(post.createTime) }}</span>
                      <span class="day">{{ formatDay(post.createTime) }}</span>
                    </div>
                    <div class="post-info">
                      <h3 class="post-title">{{ post.title }}</h3>
                    </div>
                  </div>
                </div>
              </div>
            </div>
  
            <div v-if="loading" class="loading-state">
              <i class="fas fa-spinner fa-spin"></i>
              加载中...
            </div>
          </div>
        </main>
        <Sidebar />
      </div>
    </div>
  </template>
  
  <script>
  import { mapState, mapActions } from 'vuex'
  import gsap from 'gsap'
  import ScrollTrigger from 'gsap/ScrollTrigger'
  import Sidebar from '@/components/Sidebar/index.vue'
  import { getCategoriesApi } from '@/api/article'

  gsap.registerPlugin(ScrollTrigger)
  
  export default {
    name: 'Categories',
    components: {
      Sidebar
    },

    data() {
      return {
        activeCategory: null,
        categories:[],
        loading:false
      }
    },
    methods: {
      formatMonth(date) {
        return new Date(date).toLocaleString('zh-CN', { month: 'short' })
      },
      formatDay(date) {
        return new Date(date).getDate().toString().padStart(2, '0')
      },
      goToPost(id) {
        this.$router.push(`/post/${id}`)
      },
 
      scrollToCategory(categoryName) {
        this.activeCategory = categoryName
        const element = this.$refs[categoryName][0]
        element.scrollIntoView({ behavior: 'smooth', block: 'start' })
      },
      handleScroll() {
        const categories = this.categories
        for (let i = categories.length - 1; i >= 0; i--) {
          const element = this.$refs[categories[i].name][0]
          const rect = element.getBoundingClientRect()
          if (rect.top <= 100) {
            this.activeCategory = categories[i].name
            break
          }
        }
      }
    },
    async created() {
      const res = await getCategoriesApi()
      this.categories = res.data
    },
    mounted() {
      // 监听滚动以更新激活的分类
      window.addEventListener('scroll', this.handleScroll)
    },
    beforeDestroy() {
      window.removeEventListener('scroll', this.handleScroll)
    }
  }
  </script>
  
  <style lang="scss" scoped>
  .categories-page {
    max-width: 1400px;
    margin: 0 auto;
  }
  
  .content-layout {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 320px;
    gap: $spacing-xl * 2;
    padding: $spacing-lg;
    min-height: calc(100vh - 80px);
    align-items: start;
    
    @include responsive(lg) {
      grid-template-columns: 1fr;
      padding: $spacing-lg;
    }
    
    @include responsive(md) {
      padding: $spacing-md;
    }
  }
  
  .main-content {
    min-width: 0;
    height: 100%;
  }
  
  .content-card {
    background: var(--card-bg);
    border-radius: $border-radius-lg;
    box-shadow: $shadow-md;
    padding: $spacing-lg;
  }
  
  .categories-nav {
    position: sticky;
    top: 80px;
    z-index: 10;
    background: var(--card-bg);
    margin: -$spacing-lg;
    margin-bottom: $spacing-lg;
    padding: $spacing-md $spacing-lg;
    border-bottom: 1px solid var(--border-color);
    display: flex;
    gap: $spacing-sm;
    overflow-x: auto;
    scrollbar-width: none;
    -ms-overflow-style: none;
    border-top-left-radius: $border-radius-lg;
    border-top-right-radius: $border-radius-lg;

    &::-webkit-scrollbar {
      display: none;
    }
  
    .category-tab {
      padding: $spacing-xs $spacing-md;
      border-radius: $border-radius-lg;
      background: var(--hover-bg);
      color: var(--text-secondary);
      cursor: pointer;
      transition: all 0.3s ease;
      white-space: nowrap;
      display: flex;
      align-items: center;
      gap: $spacing-xs;
      font-size: 0.9em;
  
      i {
        font-size: 0.9em;
      }
  
      &:hover {
        transform: translateY(-2px);
      }
  
      &.active {
        background: $primary;
        color: white;
      }
    }
  }
  
  .category-group {
    margin-bottom: $spacing-xl * 2;
    scroll-margin-top: 100px;
  
    &:last-child {
      margin-bottom: 0;
    }
  }
  
  .category-header {
    margin-bottom: $spacing-lg;
    padding-bottom: $spacing-sm;
    border-bottom: 2px solid rgba($primary, 0.1);
    position: relative;
  
    &::after {
      content: '';
      position: absolute;
      bottom: -2px;
      left: 0;
      width: 50px;
      height: 2px;
      background: $primary;
    }
  
    .category-name {
      font-size: 1.5em;
      color: var(--text-primary);
      display: flex;
      align-items: center;
      gap: $spacing-sm;
  
      i {
        color: $primary;
        font-size: 1em;
        width: 24px;
        text-align: center;
        opacity: 0.8;
      }
  
      .post-count {
        font-size: 0.5em;
        color: var(--text-secondary);
        margin-left: auto;
        padding: $spacing-xs $spacing-sm;
        background: var(--hover-bg);
        border-radius: $border-radius-lg;
      }
    }
  }
  
  .post-item {
    display: flex;
    align-items: center;
    gap: $spacing-xl;
    padding: $spacing-md;
    border-radius: $border-radius-md;
    cursor: pointer;
    transition: all 0.3s ease;
  
    &:hover {
      background: var(--hover-bg);
      transform: translateX(10px);
  
      .post-date {
        border-color: $primary;
        background: rgba($primary, 0.1);
      }
    }
  
    .post-date {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: $spacing-xs;
      border: 1px solid var(--border-color);
      border-radius: $border-radius-sm;
      transition: all 0.3s ease;
      min-width: 60px;
  
      .month {
        font-size: 0.9em;
        color: var(--text-secondary);
      }
  
      .day {
        font-size: 1.2em;
        font-weight: 600;
        color: var(--text-primary);
      }
    }
  
    .post-info {
      flex: 1;
      min-width: 0;
  
      .post-title {
        color: var(--text-primary);
        font-size: 1em;
        margin-bottom: 0;
        transition: color 0.3s ease;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
  
        &:hover {
          color: $primary;
        }
      }
    }
  }
  
  .loading-state {
    text-align: center;
    padding: $spacing-xl;
    color: var(--text-secondary);
    font-size: 1.1em;
  
    i {
      margin-right: $spacing-sm;
    }
  }
  
  @include responsive(md) {
    .categories-page {
      padding: $spacing-lg;
    }
  
    .content-card {
      padding: $spacing-lg;
    }
  
    .categories-nav {
      top: 64px;
      margin: -$spacing-lg;
      margin-bottom: $spacing-lg;
      padding: $spacing-sm $spacing-lg;
    }
  }
  
  @include responsive(sm) {
    .categories-page {
      padding: $spacing-md;
    }
  
    .content-card {
      padding: $spacing-md;
      border-radius: 0;
      margin: -$spacing-md;
    }
  
    .post-item {
      gap: $spacing-md;
  
      .post-date {
        min-width: 50px;
      }
    }
  
    .categories-nav {
      margin: -$spacing-md;
      margin-bottom: $spacing-md;
      padding: $spacing-sm;
      
      .category-tab {
        padding: $spacing-xs $spacing-sm;
        font-size: 0.85em;
      }
    }
  }
  </style> 
  