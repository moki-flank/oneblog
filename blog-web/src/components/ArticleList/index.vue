<template>
  <div class="article-list-component" v-loading="loading">
    <transition-group name="post-list" tag="div" class="posts-list">
      <article v-for="(post, index) in articles" :key="post.id" class="post-item"
        :class="{ 'image-right': index % 2 === 1 }">
        <div class="post-image" @click="$emit('article-click', post.id)">
          <img v-lazy="post.cover" :key="post.cover" :alt="post.title" ref="postImage">
          <div class="image-placeholder">
            <i class="fas fa-image"></i>
          </div>
          <div class="category-tag">{{ post.categoryName }}</div>
        </div>
        <div class="post-content">
          <h3 @click="$emit('article-click', post.id)" class="underline">
            <span v-if="post.isStick" class="stick-tag">
              <i class="fas fa-thumbtack"></i>
              置顶
            </span>
            {{ post.title }}
          </h3>
          <p class="post-excerpt">{{ post.summary }}</p>
          <div class="post-meta">
            <div class="meta-left">
              <span class="author">
                <el-avatar :size="24" :src="post.avatar"></el-avatar>
                <span class="author-name">{{ post.nickname }}</span>
              </span>
              <span class="date">
                <i class="far fa-calendar"></i>
                {{ formatTime(post.createTime) }}
              </span>
              <span class="read-time">
                <i class="far fa-clock"></i>
                {{ Math.ceil(post.contentMd.split(" ").length / 300) }}分钟阅读
              </span>
            </div>
            <!-- <button class="read-more" @click="$emit('article-click', post.id)">
              阅读全文
              <i class="fas fa-arrow-right"></i>
            </button> -->
          </div>
        </div>
      </article>
    </transition-group>

    <el-empty v-if="!loading && articles.length === 0" description="暂无文章" />

    <div class="pagination-box">
      <el-pagination background v-if="articles.length" @current-change="$emit('page-change', $event)"
        :current-page="params.pageNum" :page-size="params.pageSize" layout="prev, pager, next" :total="total">
      </el-pagination>
    </div>

  </div>
</template>

<script>
import { formatTime } from '@/utils/time'
export default {
  name: 'ArticleList',
  props: {
    articles: {
      type: Array,
      required: true
    },
    loading: {
      type: Boolean,
      default: false
    },
    total: {
      type: Number,
      default: 0
    },
    params: {
      type: Object,
      default: {
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  data() {
    return {
      // 默认图片URL
    }
  },
  methods: {
    formatTime(time) {
      return formatTime(time)
    }
  }
}
</script>

<style lang="scss" scoped>
.posts-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-md * 1.5;

  @include responsive(md) {
    gap: $spacing-lg;
  }
}

.post-item {
  @include card;
  display: grid;
  grid-template-columns: 380px 1fr;
  gap: 0;
  cursor: default;
  overflow: hidden;
  transition: all 0.3s ease;
  position: relative;
  background: var(--card-bg);

  &.image-right {
    grid-template-columns: 1fr 380px;

    .post-image {
      order: 2;
      clip-path: polygon(0 0, 100% 0, 100% 100%, 15% 100%);

      .category-tag {
        left: 18%;
        right: auto;
      }

      &::after {
        left: 15%;
        right: auto;
      }
    }

    .post-content {
      order: 1;
      padding: $spacing-lg $spacing-xl * 2 $spacing-lg $spacing-xl;
    }
  }

  .post-image {
    position: relative;
    height: 240px;
    overflow: hidden;
    clip-path: polygon(0 0, 100% 0, 85% 100%, 0 100%);
    cursor: pointer;

    &::after {
      content: '';
      position: absolute;
      top: 0;
      right: 15%;
      bottom: 0;
      width: 1px;
    }

    .image-placeholder {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--hover-bg);
      color: var(--text-secondary);
      font-size: 3em;
      opacity: 0;
      transition: opacity 0.3s ease;
      z-index: 0;
    }

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
      transform: scale(1.1);
      position: relative;
      z-index: 1;

      &.fallback {
        opacity: 0.8;

        &+.image-placeholder {
          opacity: 1;
        }
      }
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
    padding-left: $spacing-md * 2;
    display: flex;
    flex-direction: column;
    justify-content: center;
    background: var(--card-bg);
    position: relative;
    z-index: 1;

    h3 {
      font-size: 1.3em;
      color: var(--text-primary);
      margin-bottom: $spacing-sm;
      line-height: 1.4;
      font-weight: 600;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
      cursor: pointer;

      &:hover {
        color: $primary;
      }

      .stick-tag {
        display: inline-flex;
        align-items: center;
        gap: $spacing-sm;
        font-size: 0.7em;
        background: $secondary;
        color: white;
        padding: 2px 8px;
        border-radius: 4px;
        margin-right: $spacing-sm;
        font-weight: normal;
        vertical-align: middle;

        i {
          font-size: 0.9em;
          transform: rotate(45deg);
        }
      }
    }

    .post-excerpt {
      color: var(--text-secondary);
      line-height: 1.8;
      margin-bottom: $spacing-md;
      flex-grow: 1;
      font-size: 1em;
      display: -webkit-box;
      -webkit-line-clamp: 4;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
      cursor: default;
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
        cursor: default;
        flex-wrap: wrap;
        align-items: center;

        .author {
          display: flex;
          align-items: center;
          gap: $spacing-sm;

          .author-name {
            font-weight: 500;
            color: var(--text-primary);
            
            &:hover {
              color: $primary;
            }
          }
        }

        span {
          display: flex;
          align-items: center;
          gap: $spacing-sm;
          white-space: nowrap;

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
        cursor: pointer;

        &:hover {
          background: darken($primary, 10%);
          transform: translateX(5px);
        }
      }
    }
  }
}

// 动画效果
.post-list-enter-active {
  transition: all 0.6s ease;
  transition-delay: calc(0.2s * var(--index));
}

.post-list-leave-active {
  transition: all 0.6s ease;
}

.post-list-enter,
.post-list-leave-to {
  opacity: 0;
  transform: translateY(30px);
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
      }
    }
  }
}

@include responsive(sm) {
  .post-item {
    .post-image {
      height: 180px;
    }

    .post-content {
      padding: $spacing-md;
      padding-top: $spacing-lg;

      h3 {
        font-size: 1.3em;
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
          gap: $spacing-sm;
        }

        .read-more {
          width: 100%;
          justify-content: center;
        }
      }
    }
  }
}
</style>