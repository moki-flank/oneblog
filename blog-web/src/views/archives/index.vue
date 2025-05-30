<template>
  <div class="archives-page">
    <div class="content-layout">
      <main class="main-content">
        <el-card class="content-card">
          <div class="timeline">
            <div 
              v-for="(item, index) in archives" 
              :key="index" 
              class="year-group"
              v-animate-on-scroll
            >
              <div 
                class="year-header"
                @click="toggleYear(item.year)"
              >
                <span class="year">{{ item.year }}</span>
                <span class="toggle-icon" :class="{ 'is-open': !collapsedYears[item.year] }">
                  <i class="fas fa-chevron-down"></i>
                </span>
              </div>
              <transition
                name="expand"
                @enter="startTransition"
                @leave="endTransition"
              >
                <div 
                  class="posts-list"
                  v-show="!collapsedYears[item.year]"
                >
                  <div 
                    v-for="post in item.posts" 
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
              </transition>
            </div>
          </div>
        </el-card>
      </main>
      <Sidebar />
    </div>
  </div>
</template>

<script>
import gsap from 'gsap'
import ScrollTrigger from 'gsap/ScrollTrigger'
import Sidebar from '@/components/Sidebar/index.vue'
import { getArchivesApi } from '@/api/article'
gsap.registerPlugin(ScrollTrigger)

export default {
  name: 'Archives',
  components: {
    Sidebar
  },
  data() {
    return {
      archives: [],
      collapsedYears: {}
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
    async loadMore() {
      await this.fetchPosts({ append: true })
    },
    toggleYear(year) {
      this.$set(this.collapsedYears, year, !this.collapsedYears[year])
    },
    startTransition(element) {
      element.style.height = 'auto'
      const height = element.scrollHeight
      element.style.height = '0px'
      // 触发回流
      element.offsetHeight
      element.style.height = height + 'px'
    },
    endTransition(element) {
      element.style.height = element.scrollHeight + 'px'
      // 触发回流
      element.offsetHeight
      element.style.height = '0px'
    }
  },
  async created() {
    const res = await getArchivesApi()
    this.archives = res.data
    this.archives.forEach(item => {
      this.$set(this.collapsedYears, item.year, false)
    })
  }
}
</script>

<style lang="scss" scoped>
.archives-page {
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


.timeline {
  position: relative;
  padding-left: $spacing-xl * 2;
  margin-top: $spacing-lg;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 2px;
    background: linear-gradient(
      to bottom,
      $primary,
      $secondary
    );
    animation: grow 1s ease-out forwards;
  }
}

.year-group {
  margin-bottom: $spacing-xl * 2;

  &:last-child {
    margin-bottom: 0;
  }
}

.year-header {
  margin-bottom: $spacing-xl;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  padding-right: $spacing-md;

  &::before {
    content: '';
    position: absolute;
    left: -$spacing-xl * 2;
    top: 50%;
    width: $spacing-lg;
    height: 2px;
    background: $primary;
    animation: slideRight 0.5s ease-out forwards;
  }

  .year {
    font-size: 1.8em;
    font-weight: 700;
    color: var(--text-primary);
  }

  .toggle-icon {
    transition: transform 0.3s ease;
    
    &.is-open {
      transform: rotate(-180deg);
    }
    
    i {
      font-size: 0.8em;
      color: var(--text-secondary);
    }
  }

  &:hover {
    .toggle-icon i {
      color: $primary;
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

@include responsive(md) {
  .archives-page {
    padding: $spacing-lg;
  }

  .content-card {
    padding: $spacing-lg;
  }

  .timeline {
    padding-left: $spacing-xl;
  }

  .year-header::before {
    left: -$spacing-xl;
  }
}

@include responsive(sm) {
  .archives-page {
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
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba($primary, 0.4);
  }
  70% {
    box-shadow: 0 0 0 10px rgba($primary, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba($primary, 0);
  }
}

@keyframes grow {
  from {
    height: 0;
  }
  to {
    height: 100%;
  }
}

@keyframes slideRight {
  from {
    width: 0;
    opacity: 0;
  }
  to {
    width: $spacing-lg;
    opacity: 1;
  }
}

.posts-list {
  overflow: hidden;
  transition: height 0.3s ease-in-out;
}

.expand-enter-active,
.expand-leave-active {
  transition: all 0.3s ease-in-out;
  overflow: hidden;
}

.expand-enter-from,
.expand-leave-to {
  opacity: 0;
  height: 0;
}

.expand-enter-to,
.expand-leave-from {
  opacity: 1;
}
</style> 