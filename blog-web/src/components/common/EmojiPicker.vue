<template>
  <div class="emoji-picker">
    <button class="tool-btn" :style="{ fontSize: size }" @click.stop="togglePanel" title="插入表情">
      <i class="far fa-smile"></i>
    </button>
    <transition name="fade">
      <div v-if="show" class="emoji-panel" v-click-outside="closePanel" @wheel.stop>
        <div class="emoji-tabs">
          <button 
            v-for="(category, key) in categories" 
            :key="key"
            class="tab-btn"
            :class="{ active: currentTab === key }"
            @click="currentTab = key"
          >
            {{ category.title }}
          </button>
          <button 
            class="tab-btn"
            :class="{ active: currentTab === 'gif' }"
            @click="currentTab = 'gif'"
          >
            动图
          </button>
        </div>
        
        <div class="emoji-content" :class="{ 'gif-grid': currentTab === 'gif' }">
          <template v-if="currentTab !== 'gif'">
            <div 
              v-for="emoji in emojiList[currentTab].emojis" 
              :key="emoji"
              class="emoji-item"
              @click="selectEmoji(emoji)"
            >
              {{ emoji }}
            </div>
          </template>
          
          <template v-else>
            <div
              v-for="(url, name) in emojiList.gif"
              :key="name"
              class="gif-item"
              @click="selectGif(name, url)"
            >
              <img :src="url" :alt="name" />
            </div>
          </template>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import emojiList from '@/assets/emoji.json'

export default {
  name: 'EmojiPicker',
  props: {
    size: {
      type: String,
      default: '16px'
    }
  },
  data() {
    return {
      show: false,
      currentTab: 'people',
      emojiList
    }
  },
  computed: {
    categories() {
      const { gif, ...rest } = this.emojiList
      return rest
    }
  },
  methods: {
    togglePanel(e) {
      e.stopPropagation()
      this.show = !this.show
    },
    closePanel() {
      this.show = false
    },
    selectEmoji(emoji) {
      this.$emit('select', emoji)
      this.closePanel()
    },
    selectGif(name, url) {
      this.$emit('select', `![${name}](${url})`)
      this.closePanel()
    }
  }
}
</script>

<style lang="scss" scoped>
.emoji-picker {
  position: relative;

  .tool-btn {
    padding: $spacing-xs;
    width: 32px;
    height: 32px;
    border: none;
    background: none;
    color: var(--text-secondary);
    cursor: pointer;
    transition: all 0.3s ease;
    border-radius: $border-radius-sm;
    display: flex;
    align-items: center;
    justify-content: center;
    &:hover {
      color: $primary;
      background: var(--hover-bg);
    }
  }

  .emoji-panel {
    position: absolute;
    bottom: calc(100% + 8px);
    left: 0;
    background: var(--card-bg);
    border-radius: $border-radius-lg;
    box-shadow: $shadow-lg;
    border: 1px solid var(--border-color);
    z-index: 1000;
    width: 320px;

    &::before {
      content: '';
      position: absolute;
      bottom: -5px;
      left: 10px;
      width: 10px;
      height: 10px;
      background: var(--card-bg);
      border-left: 1px solid var(--border-color);
      border-top: 1px solid var(--border-color);
      transform: rotate(225deg);
    }

    .emoji-tabs {
      display: flex;
      border-bottom: 1px solid var(--border-color);
      padding: $spacing-xs;
      gap: $spacing-xs;
      flex-wrap: wrap;

      .tab-btn {
        padding: $spacing-xs $spacing-sm;
        border: none;
        background: none;
        color: var(--text-secondary);
        cursor: pointer;
        transition: all 0.3s ease;
        border-radius: $border-radius-sm;
        font-size: 0.9em;
        white-space: nowrap;
        flex-shrink: 0;

        &.active {
          color: $primary;
          background: var(--hover-bg);
        }

        &:hover:not(.active) {
          color: $primary;
        }
      }
    }

    .emoji-content {
      padding: $spacing-sm;
      display: grid;
      grid-template-columns: repeat(8, 1fr);
      gap: $spacing-xs;
      overflow-y: auto;
      height: 200px;
      overscroll-behavior: contain;

      &.gif-grid {
        grid-template-columns: repeat(4, 1fr);
        gap: $spacing-sm;
      }

      .emoji-item {
        width: 32px;
        height: 32px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        border-radius: $border-radius-sm;
        transition: all 0.3s ease;
        font-size: 1.2em;

        &:hover {
          background: var(--hover-bg);
          transform: scale(1.2);
        }
      }
    }
  }
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter, .fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

.emoji-tabs {
  display: flex;
  border-bottom: 1px solid #eee;
  margin-bottom: 10px;
}

.tab {
  padding: 5px 15px;
  cursor: pointer;
}

.tab.active {
  color: #409eff;
  border-bottom: 2px solid #409eff;
}

.gif-item {
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  
  img {
    width: 60px;
    height: 60px;
    object-fit: cover;
    border-radius: 4px;
    transition: transform 0.2s ease;
    
    &:hover {
      transform: scale(1.1);
    }
  }
}
</style> 