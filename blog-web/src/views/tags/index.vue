<template>
  <div class="tags-page">
    <div class="content-layout">
      <main class="main-content">
        <el-card class="content-card">
          <div class="page-header">
            <h3>标签云</h3>
            <p class="subtitle">共 {{ tags.length }} 个标签</p>
          </div>

          <div class="tags-cloud" ref="tagsCloud">
            <div 
              v-for="tag in tags" 
              :key="`tag-${tag.name}`"
              class="tag-item"
              :class="{ active: activeTag === tag.name }"
              :style="{
                '--tag-color': `hsl(${(tag.name.length * 40) % 360}, 70%, 60%)`,
                '--animation-delay': `${Math.random() * 0.5}s`
              }"
              @click="selectTag(tag.name,tag.id)"
            >
              <div class="tag-background"></div>
              <i class="fas fa-tag"></i>
              <span class="tag-name">{{ tag.name }}</span>
              <span class="tag-count">{{ tag.articleNum }}</span>
            </div>
          </div>

      
        </el-card>
        <div v-if="activeTag" class="tag-posts" ref="postsSection">
            <div class="tag-header">
              <div class="header-line"></div>
              <h2>
                <i class="fas fa-bookmark"></i>
                {{ activeTag }}
                <span class="post-count">
                  <span class="count-number">{{ total }}</span>
                  篇文章
                </span>
              </h2>
            </div>
            
            <div class="articles-wrapper" v-loading="loading">
              <ArticleList
                :articles="selectedTagPosts"
                :total="total"
                :params="params"
                @article-click="goToPost"
                @page-change="changePage"
              />
            </div>
          </div>
      </main>
      <Sidebar class="sidebar-container" />
    </div>
  </div>
</template>

<script>
import ArticleList from '@/components/ArticleList/index.vue'
import Sidebar from '@/components/Sidebar/index.vue'
import { getTagsApi } from '@/api/tags'
import { getArticlesApi } from '@/api/article'


export default {
  name: 'Tags',
  components: {
    ArticleList,
    Sidebar
  },
  data() {
    return {
      activeTag: null,
      tags:[],
      selectedTagPosts:[],
      total:0,
      totatlCount:0,
      loading:false,
      params:{
        pageNum:1,
        pageSize:10,
        tagId:null
      }
    }
  },
  methods: {
    /**
     * 跳转文章
     */
    goToPost(id) {
      this.$router.push(`/post/${id}`)
    },
    /**
     * 选择标签
     */
    async selectTag(tagName,tagId) {
      if(this.activeTag === tagName){
        return
      }
      this.activeTag = this.activeTag === tagName ? null : tagName
      this.params.tagId = tagId
      this.params.pageNum = 1
      this.getArticleList()
    },
    /**
     * 切换页码
     */
    changePage(pageNum) {
      this.params.pageNum = pageNum
      this.getArticleList()
      window.scrollTo({
        top: this.$refs.postsSection?.offsetTop - 80,
        behavior: 'smooth'
      })
    },
    /**
     * 获取文章列表
     */
    async getArticleList() {
      this.loading = true
      const res = await getArticlesApi(this.params)
      this.selectedTagPosts = res.data.records
      this.total = res.data.total
      this.loading = false
    },
  
  },
  async created() {
    const res = await getTagsApi()
    this.tags = res.data
    if(this.$route.query.tagId){
      this.selectTag(this.$route.query.tagName,this.$route.query.tagId)
    }else{
      if (this.tags.length) {
        const randomTag = this.tags[0]
        this.selectTag(randomTag.name, randomTag.id)
      }
    }
  },
}
</script>

<style lang="scss" scoped>
.tags-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: $spacing-lg 0;
  min-height: calc(100vh - 80px);
  position: relative;
  
  &::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: 
      radial-gradient(circle at 10% 20%, rgba($primary, 0.03) 0%, transparent 20%),
      radial-gradient(circle at 90% 80%, rgba(adjust-hue($primary, 60deg), 0.03) 0%, transparent 20%);
    pointer-events: none;
    z-index: -1;
  }
}

.content-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 380px;
  gap: $spacing-xl;
  padding: 0 $spacing-lg;
  min-height: 100vh;
  align-items: start;
  
  @include responsive(lg) {
    grid-template-columns: 1fr;
    padding: $spacing-md;
  }
}

.main-content {
  position: relative;
}

.page-header {
  margin-bottom: $spacing-sm;
  text-align: center;
  position: relative;
  padding: $spacing-xl 0;
  overflow: hidden;
  
  h3 {
    font-size: 1.6em;
    font-weight: 800;
    background: linear-gradient(120deg, $primary, adjust-hue($primary, 60deg));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    margin-bottom: $spacing-sm;
    letter-spacing: 2px;
    text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.1);
    position: relative;
    display: inline-block;
  }
  
  .subtitle {
    color: var(--text-secondary);
    font-size: 1.2em;
    opacity: 0.8;
    display: inline-block;
    padding: $spacing-xs $spacing-lg;
  }
}

.tags-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-md;
  margin-bottom: $spacing-sm;
  padding: 0 $spacing-md $spacing-md;
  justify-content: center;

  .tag-item {
    padding: $spacing-sm $spacing-sm;
    border-radius: $border-radius-md;
    color: var(--text-primary);
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    font-size: 1em;
    border: 2px solid transparent;
    position: relative;
    overflow: hidden;
    animation: tagAppear 0.4s ease-out backwards;
    animation-delay: var(--animation-delay);
    background: var(--bg-secondary);
    
    i {
      font-size: 0.9em;
      color: var(--tag-color);
      transition: transform 0.3s ease;
    }

    .tag-name {
      position: relative;
      z-index: 1;
    }

    .tag-count {
      font-size: 0.85em;
      color: var(--text-secondary);
      background: rgba(var(--tag-color), 0.1);
      padding: 3px 12px;
      border-radius: $border-radius-lg;
      transition: all 0.3s ease;
    }

    &:hover {
      transform: translateY(-2px);
      border-color: var(--tag-color);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      
      i {
        transform: rotate(-10deg);
      }
    }

    &.active {
      background: var(--tag-color);
      color: white;
      box-shadow: 0 4px 12px rgba(var(--tag-color), 0.2);
      
      i {
        color: white;
      }
      
      .tag-count {
        background: rgba(255, 255, 255, 0.25);
        color: white;
      }
    }
  }
}

.tag-header {
  margin-bottom: $spacing-xl;
  padding: $spacing-md $spacing-xl;
  background: var(--bg-secondary);
  border-radius: $border-radius-lg;
  position: relative;
  overflow: hidden;
  
  .header-line {
    position: absolute;
    top: 0;
    left: 0;
    width: 4px;
    height: 100%;
    background: linear-gradient(to bottom, $primary, adjust-hue($primary, 40deg));
    
    &::before,
    &::after {
      content: '';
      position: absolute;
      left: 0;
      width: 100%;
      height: 10px;
      background: white;
      filter: blur(4px);
    }
    
    &::before {
      top: 0;
    }
    
    &::after {
      bottom: 0;
    }
  }

  h2 {
    font-size: 1.6em;
    color: var(--text-primary);
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    margin: 0;

    i {
      color: $primary;
      font-size: 0.9em;
      transition: transform 0.3s ease;
    }

    .post-count {
      font-size: 0.6em;
      color: var(--text-secondary);
      margin-left: auto;
      padding: $spacing-xs $spacing-md;
      background: linear-gradient(120deg, rgba($primary, 0.1), rgba(adjust-hue($primary, 60deg), 0.1));
      backdrop-filter: blur(4px);
      border: 1px solid rgba($primary, 0.15);
      transition: all 0.3s ease;
      border-radius: $border-radius-lg;
      letter-spacing: 0.5px;
      display: flex;
      align-items: center;
      gap: $spacing-xs;
      
      .count-number {
        color: $primary;
        font-weight: 600;
        font-size: 1.2em;
      }

      &:hover {
        border-color: rgba($primary, 0.4);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba($primary, 0.15);
      }
    }
  }
}

.articles-wrapper {
  padding: 0 $spacing-lg;
  animation: slideUp 0.6s ease-out;
}

.sidebar-container {
  position: sticky;
  top: 80px;
  /* 确保在移动端时取消sticky效果 */
  @include responsive(lg) {
    position: relative;
    top: 0;
    height: auto;
    overflow-y: visible;
  }
}

@keyframes tagAppear {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@include responsive(sm) {
  .tags-page {
    padding: $spacing-md 0;
  }

  .content-card {
    border-radius: $border-radius-lg;
    margin: 0;
  }

  .page-header {
    padding: $spacing-lg 0;
    
    h1 {
      font-size: 2.4em;
    }

  }

  .tags-cloud {
    padding: 0 $spacing-md $spacing-lg;
    gap: $spacing-sm;
    
    .tag-item {
      padding: $spacing-xs $spacing-md;
      font-size: 0.9em;
    }
  }

  .tag-header {
    padding: $spacing-sm $spacing-md;
    
    h2 {
      font-size: 1.4em;
    }
  }

  .articles-wrapper {
    padding: 0 $spacing-md;
  }
}

.tag-background,
.header-waves,
.wave {
  display: none;
}
</style> 