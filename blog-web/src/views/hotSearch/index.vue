<template>
  <div class="hot-search-container">
    <!-- 搜索框部分 -->
    <div class="search-box">
      <div class="search-input">
        <el-dropdown trigger="click" @command="handleEngineChange">
          <span class="engine-selector">
            <svg-icon :icon-class="getCurrentEngine.icon"></svg-icon>
            <i class="el-icon-arrow-down"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="engine in searchEngines"
              :key="engine.name"
              :command="engine.name"
            >
              <svg-icon :icon-class="engine.icon"></svg-icon>
              {{ engine.label }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-input
          v-model="searchText"
          :placeholder="`请输入内容`"
          @keyup.enter="handleSearch"
        />
        <button class="search-btn" @click="handleSearch">
          <i class="fas fa-search"></i>
        </button>
      </div>
    </div>

    <!-- 热搜卡片网格 -->
    <div class="hot-cards-grid">
      <div
        v-for="tab in hotTabs"
        :key="tab.type"
        class="hot-card-container"
        :class="tab.type"
      >
        <div class="hot-card-header">
          <h3>{{ tab.label }}</h3>
        </div>
        <div class="hot-list" v-loading="loading">
          <div
            v-for="(item, index) in hotLists[tab.type]"
            :key="index"
            class="hot-item"
            @click="handleHotItemClick(item)"
          >
            <span class="rank" :class="getRankClass(index)">{{
              index + 1
            }}</span>
            <span class="title">{{ item.keyword }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getHotListApi } from "@/api/hot-search";
export default {
  name: "HotSearch",
  data() {
    return {
      searchText: "",
      currentEngine: "baidu",
      loading: false,
      searchEngines: [
        {
          name: "baidu",
          label: "百度",
          icon: "baidu",
          url: "https://www.baidu.com/s?wd=",
        },
        {
          name: "google",
          label: "谷歌",
          icon: "chrome",
          url: "https://www.google.com/search?q=",
        },
        {
          name: "gitee",
          label: "码云",
          icon: "gitee",
          url: "https://search.gitee.com/?q=",
        },
        {
          name: "github",
          label: "GitHub",
          icon: "github",
          url: "https://github.com/search?q=",
        },
      ],
      hotTabs: [
        { type: "weibo", label: "微博热搜", icon: "fab fa-weibo" },

        { type: "zhihu", label: "知乎热榜", icon: "fab fa-zhihu" },

        { type: "toutiao", label: "头条热榜", icon: "fas fa-fire" },

        { type: "baidu", label: "百度热搜", icon: "fas fa-chart-line" },

        { type: "csdn", label: "CSDN热榜", icon: "fas fa-code" },
      ],
      hotLists: {
        weibo: [],
        zhihu: [],
        toutiao: [],
        baidu: [],
        csdn: [],
      },
    };
  },

  created() {
    this.fetchHotList("weibo");
    this.fetchHotList("zhihu");
    this.fetchHotList("toutiao");
    this.fetchHotList("baidu");
    this.fetchHotList("csdn");
  },

  computed: {
    getCurrentEngine() {
      return this.searchEngines.find(
        (engine) => engine.name === this.currentEngine
      );
    },
  },

  methods: {
    /**
     * 搜索
     */
    handleSearch() {
      if (!this.searchText.trim()) {
        this.$message.warning("请输入搜索内容");
        return;
      }
      const engine = this.getCurrentEngine;
      window.open(engine.url + encodeURIComponent(this.searchText), "_blank");
    },

    /**
     * 获取热搜排名
     */
    getRankClass(index) {
      if (index < 3) return `rank-${index + 1}`;
      return "";
    },

    /**
     * 获取热搜列表
     */
    async fetchHotList(type) {
      this.loading = true;
      try {
        // 这里替换为实际的API调用
        const res = await getHotListApi(type);
        this.hotLists[type] = res.data.data;
      } catch (error) {
        this.$message.error("获取热搜数据失败");
      } finally {
        this.loading = false;
      }
    },

    /**
     * 点击热搜
     */
    handleHotItemClick(item) {
      if (item.url) {
        window.open(item.url, "_blank");
      }
    },

    /**
     * 切换搜索引擎
     */
    handleEngineChange(newEngine) {
      this.currentEngine = newEngine;
    },
  },
};
</script>

<style lang="scss" scoped>
.hot-search-container {
  max-width: 1800px;
  margin: 0 auto;
  margin-top: $spacing-lg;
}

.search-box {
  margin-bottom: $spacing-xl;
  max-width: 800px;
  margin: 0 auto $spacing-xl;
  .search-input {
    display: flex;
    align-items: center;
    background: var(--card-bg);
    border-radius: $border-radius-lg;
    padding: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    .engine-selector {
      display: flex;
      align-items: center;
      padding: 0 $spacing-md;
      cursor: pointer;
      .svg-icon {
        margin-right: $spacing-sm;
        font-size: 20px;
      }
    }
    :deep(.el-input__inner) {
      border: none !important;
      &:focus {
        outline: none;
      }
    }
    .search-btn {
      background: none;
      border: none;
      padding: $spacing-md;
      cursor: pointer;
      color: var(--text-primary);
      &:hover {
        color: $primary;
      }
    }
  }
}

.hot-cards-grid {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-md;
  margin: 0 100px;
  
  .hot-card-container {
    flex: 0 0 calc(20% - #{$spacing-md} * 4 / 5);
    margin-bottom: $spacing-md;
  }
}

@include responsive(lg) {
  .hot-cards-grid {
    margin: 0 $spacing-xl;
    .hot-card-container {
      flex: 0 0 calc(25% - #{$spacing-md} * 3 / 4);
    }
  }
}

@include responsive(md) {
  .hot-cards-grid {
    margin: 0 $spacing-lg;
    .hot-card-container {
      flex: 0 0 calc(33.333% - #{$spacing-md} * 2 / 3);
    }
  }
}

@include responsive(sm) {
  .hot-cards-grid {
    gap: $spacing-md;
    padding: 0;
    margin: 0 10px !important;
    .hot-card-container {
      flex: 0 0 100%;
    }
  }
}

@include responsive(xs) {
  .hot-cards-grid {
    .hot-card-container {
      flex: 0 0 100%;
    }
  }
}

.hot-card-container {
  overflow: hidden;
  position: relative;
  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 120px;
  }

  &.weibo::before {
    background: linear-gradient(135deg, #ff6b6b, #ff8787);
  }

  &.baidu::before {
    background: linear-gradient(135deg, #4dabf7, #74c0fc);
  }

  &.zhihu::before {
    background: linear-gradient(135deg, #228be6, #4dabf7);
  }

  &.csdn::before {
    background: linear-gradient(135deg, #ff922b, #ffa94d);
  }

  &.toutiao::before {
    background: linear-gradient(135deg, #ff6b6b, #ff8787);
  }

  .hot-card-header {
    position: relative;
    padding: $spacing-lg $spacing-lg;
    color: #fff;
    text-align: center;
    z-index: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-md;

    i {
      font-size: 1.6rem;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
    }

    h3 {
      margin: 0;
      font-size: 1.3rem;
      font-weight: 600;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
    }
  }

  .hot-list {
    padding: $spacing-sm;
    background: var(--card-bg);
    position: relative;
    z-index: 1;

    .hot-item {
      display: flex;
      padding: $spacing-md;
      cursor: pointer;
      transition: all 0.3s ease;
      border-radius: $border-radius-sm;
      margin-bottom: 8px;
      border: 1px solid transparent;
      &:hover {
        background: rgba($primary, 0.05);
        transform: translateX(5px);
        border-color: rgba($primary, 0.1);

        .rank {
          transform: scale(1.05);
        }

        .title {
          color: $primary;
        }
      }

      .rank {
        min-width: 28px;
        height: 28px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: $spacing-md;
        font-weight: bold;
        font-size: 0.95rem;
        border-radius: 8px;
        flex-shrink: 0;
        transition: all 0.3s ease;
        box-shadow: 2px 2px 6px rgba(0, 0, 0, 0.1);
        color: var(--text-primary);

        &.rank-1 {
          background: linear-gradient(135deg, #ff6b6b, #ff8787);
          color: #fff;
        }

        &.rank-2 {
          background: linear-gradient(135deg, #4dabf7, #74c0fc);
          color: #fff;
        }

        &.rank-3 {
          background: linear-gradient(135deg, #ffd43b, #fab005);
          color: #fff;
        }
      }

      .title {
        flex: 1;
        font-size: 0.95rem;
        line-height: 1.5;
        color: var(--text-primary);
        transition: all 0.3s ease;
        word-break: break-all;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;

        overflow: hidden;
      }
    }
  }
}
</style>
