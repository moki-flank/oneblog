<template>
  <aside class="sidebar">
    <el-card class="author-card">
      <div class="floating-cards">
        <span v-for="n in 12" :key="n" class="card-particle"></span>
      </div>
      <div class="card-header">
        <div class="author-avatar">
          <el-avatar class="avatar" :src="$store.state.webSiteInfo.authorAvatar" alt="作者头像" />
        </div>
        <div class="status-badge">
          <span>在线</span>
        </div>
      </div>
      <div class="author-info">
        <h3>{{ $store.state.webSiteInfo.author }}</h3>
        <p class="bio">{{ $store.state.webSiteInfo.authorInfo }}</p>
      </div>
      <div class="social-links">
        <div v-for="item in socialLinksWithData" :key="item.type">
          <el-tooltip placement="top" :content="item.content">
            <a v-if="$store.state.webSiteInfo.showList.indexOf(item.type) !== -1" href="javascript:void(0)"
                :title="item.title" :class="`social-btn ${item.type}`" @click="copyToClipboard(item)">
                <i :class="item.icon"></i>
            </a>
          </el-tooltip>
        </div>

      </div>
    </el-card>

    <el-card class="section announcement" v-if="announcements.length">
      <h3>
        <i class="fas fa-bullhorn"></i>
        公告
      </h3>
      <div class="announcement-content">
        <div class="announcement-item" v-for="(item, index) in announcements" :key="index">
          <!-- <i :class="item.icon"></i> -->
          <span v-html="item.content"></span>
        </div>
      </div>
    </el-card>

    <el-card class="section" v-if="hot.length > 0">
      <h3>
        <i class="fas fa-star"></i>
        推荐文章
      </h3>
      <div class="post-list">
        <router-link v-for="post in hot" :key="post.id" :to="`/post/${post.id}`" class="post-item">
          <img v-lazy="post.cover" :key="post.cover" :alt="post.title" @error="handleImageError">
          <div class="post-meta">
            <h4>{{ post.title }}</h4>
            <time>{{ post.createTime }}</time>
          </div>
        </router-link>
      </div>
    </el-card>

    <!-- <div class="section">
      <h3>标签</h3>
      <div class="tags">
        <a 
          v-for="tag in tags" 
          :key="tag.name"
          :href="`#${tag.name}`"
          class="tag"
        >
          {{ tag.name }}
        </a>
      </div>
    </div> -->
  </aside>
</template>

<script>
import { getRecommendArticlesApi } from '@/api/article'

export default {
  name: 'Sidebar',
  data() {
    return {
      hot: [],
      announcements: [],
      socialLinks: [
        {
          icon: 'fab fa-github',
          type: 'github',
          content: '点击跳转GitHub主页',
          icCopy: false
        },
        {
          icon: 'fab fa-git-alt',
          type: 'gitee',
          content: '点击跳转GitEE主页',
          icCopy: false
        },
        {
          icon: 'fab fa-qq',
          title: 'QQ',
          type: 'qq',
          content: '点击复制QQ号',
          icCopy: true
        },
        {
          icon: 'fas fa-users',
          title: 'QQ群',
          type: 'qqGroup',
          content: '点击复制QQ群号',
          icCopy: true
        },
        {
          icon: 'fas fa-at',
          title: '邮箱',
          type: 'email',
          content: '点击复制邮箱',
          icCopy: true
        },
        {
          icon: 'fab fa-weixin',
          title: '微信',
          type: 'wechat',
          content: '点击复制微信号',
          icCopy: true
        }
      ],
      tags: [],
    }
  },
  computed: {
    socialLinksWithData() {
      return this.socialLinks.map(item => {
        const linkMap = {
          github: this.$store.state.webSiteInfo.github,
          gitee: this.$store.state.webSiteInfo.gitee,
          qq: this.$store.state.webSiteInfo.qqNumber,
          qqGroup: this.$store.state.webSiteInfo.qqGroup,
          email: this.$store.state.webSiteInfo.email,
          wechat: this.$store.state.webSiteInfo.wechat
        }
        return {
          ...item,
          link: linkMap[item.type]
        }
      })
    }
  },
  watch: {
    '$store.state.notice'(val) {
      if (val && val.right) {
        this.announcements = val.right
      }
    } 
  },
  mounted() {
    getRecommendArticlesApi().then(res => {
      this.hot = res.data
    })
    // 初始化公告数据
    if (this.$store.state.notice && this.$store.state.notice.right) {
      this.announcements = this.$store.state.notice.right
    }
  },
  methods: {
    /**
     * 处理图片加载失败
     */
    handleImageError(e) {
      e.target.src = this.$store.state.defaultImage
      e.target.classList.add('fallback')
    },
    /**
     * 复制到剪贴板
     */
    copyToClipboard(item) {
      if (item.icCopy) {
        navigator.clipboard.writeText(item.link).then(() => {
          this.$message.success(`${item.title}账号已复制到剪贴板`);
        }).catch(() => {
          this.$message.error('复制失败，请手动复制');
        });
      } else {
        window.open(item.link, '_blank')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebar {
  position: sticky;
  top: 80px;
  width: 100%;
  max-width: 320px;

  .author-card {

    padding: $spacing-md;
    margin-bottom: $spacing-lg;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    position: relative;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 120px;
      background-image: url('https://img.shiyit.com/beijing.jpg');
      background-size: cover;
      background-position: center;
      transition: opacity 0.3s ease;
      border-top-left-radius: $border-radius-md;
      border-top-right-radius: $border-radius-md;
    }

    &:hover::before {
      opacity: 0.2;
    }

    .card-header {
      position: relative;
      margin-bottom: 20px;
      display: flex;
      justify-content: center;

      .author-avatar {
        width: 88px;
        height: 88px;
        position: relative;

        .avatar {
          width: 100%;
          height: 100%;
          border-radius: 50%;
          object-fit: cover;
          border: 3px solid #fff;
          box-shadow: 0 4px 12px rgba(99, 102, 241, 0.15);
          transition: transform 0.3s ease;

          &:hover {
            transform: scale(1.05);
          }
        }
      }

      .status-badge {
        position: absolute;
        bottom: 0;
        right: 50%;
        transform: translateX(32px);
        background: linear-gradient(135deg, #10b981, #059669);
        border-radius: 12px;
        padding: 4px 12px;
        font-size: 0.75rem;
        color: white;
        box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);

        span {
          display: flex;
          align-items: center;
          gap: 4px;

          &::before {
            content: '';
            display: inline-block;
            width: 6px;
            height: 6px;
            border-radius: 50%;
            background: #fff;
          }
        }
      }
    }

    .author-info {
      text-align: center;
      margin-bottom: 20px;

      h3 {
        font-size: 1.25rem;
        font-weight: 700;
        color: $primary;
        -webkit-background-clip: text;
        margin-bottom: 8px;
      }

      .bio {
        font-size: 0.9rem;
        color: var(--text-secondary);
        line-height: 1.6;
        margin-bottom: 16px;
      }
    }

    .social-links {
      display: flex;
      justify-content: center;
      gap: 10px;
      margin-top: 20px;

      .social-btn {
        width: 36px;
        height: 36px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 12px;
        background: rgba(99, 102, 241, 0.05);
        font-size: 1.1rem;
        transition: all 0.3s ease;
        position: relative;
        text-decoration: none;
      }

      .qq {
        color: #60a5fa;

        &:hover {
          background: #60a5fa;
          color: white;
        }
      }

      .qqGroup {
        color: #e1c235;

        &:hover {
          background: #e1c235;
          color: white;
        }
      }

      .github {
        color: #000;

        &:hover {
          background: #000;
          color: white;
        }
      }

      .gitee {
        color: #ee3434;

        &:hover {
          background: #da3535;
          color: white;
        }
      }

      .email {
        color: #d872a7;

        &:hover {
          background: #d872a7;
          color: white;
        }
      }

      .wechat {
        color: #10b981;

        &:hover {
          background: #10b981;
          color: white;
        }
      }
    }

    .floating-cards {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      pointer-events: none;
      overflow: hidden;
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:hover .floating-cards {
      opacity: 1;
    }

    .card-particle {
      position: absolute;
      width: 8px;
      height: 8px;
      background: white;
      border-radius: 2px;
      opacity: 0;
      animation: fall 3s ease-in-out infinite;

      @for $i from 1 through 12 {
        &:nth-child(#{$i}) {
          left: #{random(100)}#{"%"};
          animation-delay: #{random(3000)}ms;
          background: #{nth(
 (#6366f1,
            #8b5cf6,
            #ec4899,
            #10b981,
            #f59e0b,
            #ef4444),
          random(6))
        }

        ;
        transform: rotate(#{$i * 30}deg);
      }
    }
  }
}

.section {
  margin-bottom: $spacing-lg;

  h3 {
    font-size: 1.1rem;
    font-weight: 600;
    color: #6366f1;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 2px solid rgba(99, 102, 241, 0.1);

    &::before {
      content: '';
      display: inline-block;
      width: 4px;
      height: 16px;
      background: linear-gradient(to bottom, #6366f1, #8b5cf6);
      margin-right: 8px;
      border-radius: 2px;
      vertical-align: middle;
      transform: translateY(-1px);
    }
  }

  .post-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
    counter-reset: post-counter;

    .post-item {
      display: flex;
      gap: 16px;
      text-decoration: none;
      transition: all 0.3s ease;
      position: relative;
      padding-left: 32px;

      &::before {
        content: counter(post-counter);
        counter-increment: post-counter;
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 22px;
        height: 22px;
        background: var(--number-bg, #f87171);
        color: white;
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 13px;
        font-weight: 600;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }

      &:nth-child(2)::before {
        --number-bg: #fbbf24;
      }

      &:nth-child(3)::before {
        --number-bg: #60a5fa;
      }

      &:nth-child(n+4)::before {
        --number-bg: #9ca3af;
      }

      &:hover {
        transform: translateX(4px);

        h4 {
          color: #6366f1;
        }

        img {
          transform: scale(1.03);
        }
      }

      img {
        width: 100px;
        height: 70px;
        border-radius: 6px;
        object-fit: cover;
        transition: transform 0.3s ease;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

        &.fallback {
          opacity: 0.7;
        }
      }

      .post-meta {
        flex: 1;
        min-width: 0;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        h4 {
          font-size: 0.95rem;
          font-weight: 500;
          color: var(--text-primary);
          margin-bottom: 6px;
          overflow: hidden;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          line-height: 1.4;
          transition: color 0.3s ease;
        }

        time {
          font-size: 0.8rem;
          color: #8b8b8b;
          display: flex;
          align-items: center;
          gap: 4px;

          &::before {
            content: '\f017';
            font-family: 'Font Awesome 5 Free';
            font-size: 0.75rem;
            opacity: 0.8;
          }
        }
      }
    }
  }

  .tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    .tag {
      font-size: 0.9rem;
      color: #6366f1;
      text-decoration: none;
      padding: 4px 12px;
      border-radius: 16px;
      background: rgba(99, 102, 241, 0.05);
      border: 1px solid rgba(99, 102, 241, 0.1);
      font-weight: 500;
      transition: all 0.2s;

      &:hover {
        background: linear-gradient(135deg, #6366f1, #8b5cf6);
        color: white;
        border-color: transparent;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(99, 102, 241, 0.2);
      }
    }
  }
}

.announcement {
  h3 {
    i {
      margin-right: 8px;
      color: #f59e0b;
      animation: shake 1.5s ease-in-out infinite;
    }
  }

  .announcement-content {
    .announcement-item {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      padding: 12px 0;
      border-bottom: 1px dashed rgba(99, 102, 241, 0.1);

      &:last-child {
        border-bottom: none;
        padding-bottom: 0;
      }

      &:first-child {
        padding-top: 0;
      }

      i {
        font-size: 1rem;
        color: #6366f1;
        flex-shrink: 0;
        margin-top: 3px;
      }

      span {
        font-size: 0.9rem;
        color: var(--text-secondary);
        line-height: 1.6;
      }

      &:hover {
        i {
          transform: scale(1.1);
        }

        span {
          color: var(--text-primary);
        }
      }

      i,
      span {
        transition: all 0.3s ease;
      }
    }
  }
}

@media (prefers-color-scheme: dark) {
  .sidebar {
    .section {
      .post-item {
        &::before {
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
        }

        img {
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
        }

        .post-meta {
          time {
            color: #777;
          }
        }

        &:hover h4 {
          color: #818cf8;
        }
      }
    }
  }
}
}

@include responsive(lg) {
  .sidebar {
    display: none;
  }
}

@keyframes float {
  0% {
    transform: translateY(0px);
  }

  50% {
    transform: translateY(-5px);
  }

  100% {
    transform: translateY(0px);
  }
}

.author-avatar img {
  animation: float 3s ease-in-out infinite;
}

@keyframes fall {
  0% {
    transform: translateY(-20px) rotate(0deg);
    opacity: 0;
  }

  20% {
    opacity: 0.8;
  }

  80% {
    opacity: 0.8;
  }

  100% {
    transform: translateY(300px) rotate(360deg);
    opacity: 0;
  }
}

@keyframes shake {
  0% {
    transform: rotate(0deg);
  }

  25% {
    transform: rotate(-10deg);
  }

  75% {
    transform: rotate(10deg);
  }

  100% {
    transform: rotate(0deg);
  }
}

.fa-star {
  color: #ef5151;
}
</style>