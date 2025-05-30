<template>
  <div class="home" v-loading="loading">

    <div class="content-layout">
      <main class="home-main-content">
        <Carousel v-if="carouselSlides?.length > 0"
          :slides="carouselSlides" 
          @click="goToPost"
        />
        <MomentsList />
        <ArticleList
          :articles="articleList"
          :loading="loading"
          :total="total"
          :params="params"
          @article-click="goToPost"
          @page-change="changePage"
        />
      </main>
      <Sidebar />
    </div>
  </div>
</template>

<script>
import ArticleList from '@/components/ArticleList/index.vue'
import Carousel from '@/components/common/Carousel.vue'
import Sidebar from '@/components/Sidebar/index.vue'
import MomentsList from '@/components/MomentsList/index.vue'
import { getArticlesApi,getCarouselArticlesApi } from '@/api/article'

export default {
  name: 'Home',
  components: {
    ArticleList,
    Carousel,
    Sidebar,
    MomentsList,
  },
  data() {
    return {
      total: 0,
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      articleList: [],
      carouselSlides: [],
      loading: false
    }
  },
  methods: {
    /**
     * 跳转到文章详情
     * @param {number} id 文章id
     */
    goToPost(id) {
      this.$router.push(`/post/${id}`)
    },
    /**
     * 切换页码
     * @param {number} page 页码
     */
    changePage(page) {
      this.params.pageNum = page
      this.getArticleList()
      window.scrollTo({
        top: this.$refs.postsSection?.offsetTop - 80,
        behavior: 'smooth'
      })
    },
    /**
     * 获取文章列表
     */
    getArticleList() {
      this.loading = true
      getArticlesApi(this.params).then(res => {
        if (res.data && res.data.records) {
          this.articleList = res.data.records
          this.total = res.data.total
        }
      }).catch(error => {
        console.error('Failed to fetch articles:', error)
      }).finally(() => {
        this.loading = false
      })
    },
    /**
     * 获取轮播和推荐文章
     */
    getCarouselArticles() {
      getCarouselArticlesApi().then(res => {
        this.carouselSlides = res.data
      })
    }
  },
  created() {
    this.getArticleList()
    this.getCarouselArticles()
  },

}
</script>

<style lang="scss" scoped>
.home {
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  padding: $spacing-lg;
}

.content-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: $spacing-lg * 2;
  padding: 0 $spacing-xl;
  margin-bottom: $spacing-xl * 2;
  min-height: calc(100vh - 80px);
  align-items: start;

  @include responsive(lg) {
    grid-template-columns: 1fr;
    padding: $spacing-sm;
  }
}

.home-main-content {
  min-width: 0;
  width: 100%;
  height: 100%;

  .carousel {
    margin-bottom: $spacing-xl;
    width: 100%;
    max-height: 480px;

    @include responsive(md) {
      margin-bottom: $spacing-xl;
      max-height: 280px;
      :deep(h3) {
        font-size: 1.2em;
      }
    }
  }
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-xl * 1.5;

  @include responsive(md) {
    gap: $spacing-lg;
  }
}

.post-item {
  @include card;
  display: grid;
  grid-template-columns: 380px 1fr;
  gap: 0;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
  position: relative;
  background: var(--card-bg);

  &.image-right {
    grid-template-columns: 1fr 380px;
    
    .post-image {
      order: 2;
      clip-path: polygon(15% 0, 100% 0, 100% 100%, 0 100%);

      &::after {
        left: 15%;
        right: auto;
      }
    }
    
    .post-content {
      order: 1;
      padding-right: $spacing-xl * 2;
    }
  }

  .post-image {
    position: relative;
    height: 240px;
    overflow: hidden;
    clip-path: polygon(0 0, 100% 0, 85% 100%, 0 100%);
    
    &::after {
      content: '';
      position: absolute;
      top: 0;
      right: 15%;
      bottom: 0;
      width: 1px;
    }

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
      transform: scale(1.1);
    }

    .category-tag {
      position: absolute;
      top: $spacing-md;
      right: 18%;
      padding: $spacing-sm $spacing-md;
      background: rgba($primary, 0.85);
      backdrop-filter: blur(4px);
      color: white;
      border-radius: 20px;
      font-size: 0.9em;
      font-weight: 500;
      z-index: 1;
    }
  }

  &:hover {
    .post-image img {
      transform: scale(1.15);
    }
    transform: translateY(-5px);
    box-shadow: $shadow-lg;

  }

  .post-content {
    padding: $spacing-lg $spacing-xl;
    padding-left: $spacing-xl * 2;
    display: flex;
    flex-direction: column;
    justify-content: center;
    background: var(--card-bg);
    position: relative;
    z-index: 1;

    h3 {
      font-size: 1.8em;
      color: var(--text-primary);
      margin-bottom: $spacing-sm;
      line-height: 1.4;
      font-weight: 600;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .post-excerpt {
      color: var(--text-secondary);
      line-height: 1.6;
      margin-bottom: $spacing-md;
      flex-grow: 1;
      font-size: 1.1em;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .post-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .meta-left {
        display: flex;
        gap: $spacing-lg;
        color: var(--text-secondary);
        font-size: 0.9em;

        span {
          display: flex;
          align-items: center;
          gap: $spacing-sm;

          i {
            color: $primary;
          }
        }
      }

      .read-more {
        display: inline-flex;
        align-items: center;
        gap: $spacing-sm;
        padding: $spacing-sm $spacing-lg;
        background: $primary;
        color: white;
        border: none;
        border-radius: 20px;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          background: darken($primary, 10%);
          transform: translateX(5px);
        }
      }
    }
  }
}

.load-more {
  text-align: center;
  margin-top: $spacing-xl * 2;
}

.load-more-btn {
  padding: $spacing-md $spacing-xl;
  background: white;
  border: 2px solid $primary;
  color: $primary;
  border-radius: 25px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 150px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-sm;

  &:hover:not(:disabled) {
    background: $primary;
    color: white;
    transform: translateY(-2px);
    box-shadow: $shadow-md;
  }

  &.loading {
    opacity: 0.7;
    cursor: not-allowed;
    pointer-events: none;
  }

  i {
    font-size: 1.1em;
  }
}

.section-header {
  text-align: center;
  margin-bottom: $spacing-xl;

  h2 {
    font-size: 2em;
    color: $text-primary;
    margin-bottom: $spacing-lg;
  }
}

.category-filters {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: $spacing-md;
}

.category-btn {
  padding: $spacing-sm $spacing-lg;
  border: none;
  border-radius: 25px;
  background: #f3f4f6;
  color: $text-secondary;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;

  &.active {
    background: $primary;
    color: white;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: $shadow-md;
  }
}

@include responsive(md) {
  .post-item {
    grid-template-columns: 1fr;
    margin: 0 -$spacing-sm;
    border-radius: 0;

    &.image-right {
      grid-template-columns: 1fr;
    }

    .post-image {
      height: 240px;
      clip-path: polygon(0 0, 100% 0, 100% 85%, 0 100%);

      &::after {
        display: none;
      }

      .category-tag {
        right: $spacing-md;
      }
    }

    .post-content {
      padding: $spacing-lg;
      padding-top: $spacing-xl;

      h3 {
        font-size: 1.5em;
        -webkit-line-clamp: 2;
      }
    }
  }

  .pagination {
    margin-top: $spacing-xl;
  }
}

@include responsive(sm) {
  .content-layout {
    padding: 0;
    margin-top: $spacing-sm;
  }

  .post-item {
    .post-image {
      height: 180px;
    }

    .post-content {
      padding: $spacing-md;
      padding-top: $spacing-lg;

      h3 {
        font-size: 1.3em;
        -webkit-line-clamp: 2;
      }

      .post-excerpt {
        font-size: 1em;
        -webkit-line-clamp: 2;
        margin-bottom: $spacing-sm;
      }

      .post-meta {
        flex-direction: column;
        gap: $spacing-sm;
        align-items: flex-start;

        .meta-left {
          flex-direction: column;
          gap: $spacing-sm;
        }

        .read-more {
          width: 100%;
          justify-content: center;
        }
      }
    }
  }

  .pagination {
    margin-top: $spacing-lg;
    
    .page-numbers {
      display: none;
    }
  }
}

// 优化动画效果
.post-list-enter-active {
  transition: all 0.6s ease;
  transition-delay: calc(0.2s * var(--index));
}

.post-list-leave-active {
  transition: all 0.6s ease;
}

.post-list-enter, .post-list-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

.loading-state {
  text-align: center;
  padding: $spacing-xl;
  color: var(--text-secondary);
  font-size: 1.2em;
  
  i {
    margin-right: $spacing-sm;
  }
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: $spacing-md;
  margin-top: $spacing-xl * 2;

  .page-btn {
    width: 40px;
    height: 40px;
    border: none;
    border-radius: 50%;
    background: var(--card-bg);
    color: var(--text-primary);
    cursor: pointer;
    transition: all 0.3s ease;
    @include card;

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }

    &:not(:disabled):hover {
      color: $primary;
      transform: translateY(-2px);
    }
  }

  .page-numbers {
    display: flex;
    gap: $spacing-sm;

    .page-number {
      width: 40px;
      height: 40px;
      border: none;
      border-radius: 50%;
      background: var(--card-bg);
      color: var(--text-primary);
      cursor: pointer;
      transition: all 0.3s ease;
      font-weight: 500;



      &.active {
        background: $primary;
        color: white;
      }
    }
  }
}

.empty-state {
  text-align: center;
  padding: $spacing-xl * 2;
  color: var(--text-secondary);

  i {
    font-size: 3em;
    margin-bottom: $spacing-md;
    color: $primary;
  }

  p {
    font-size: 1.1em;
  }
}

/* 添加过渡动画样式 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style> 
