<template>
  <div class="color-picker">
    <button class="tool-btn" :style="{ fontSize: size }" @click.stop="togglePanel" title="文字颜色">
      <i class="fas fa-palette" :style="{ color: currentColor }"></i>
    </button>
    <transition name="fade">
      <div v-if="show" class="color-panel" v-click-outside="closePanel">
        <div class="color-grid">
          <button
            v-for="color in colors"
            :key="color.value"
            class="color-item"
            :style="{ background: color.value }"
            :title="color.name"
            @click="selectColor(color.value)"
          >
            <i v-if="currentColor === color.value" class="fas fa-check"></i>
          </button>
        </div>
        <div class="panel-footer">
          <button class="reset-btn" @click="resetColor">
            <i class="fas fa-undo"></i>
            重置颜色
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: 'ColorPicker',
  props: {
    size: {
      type: String,
    }
  },
  data() {
    return {
      show: false,
      currentColor: '',
      colors: [
        { name: '红色', value: '#f56c6c' },
        { name: '粉色', value: '#f093fb' },
        { name: '橙色', value: '#e6a23c' },
        { name: '黄色', value: '#f4d03f' },
        { name: '绿色', value: '#67c23a' },
        { name: '青色', value: '#36cfc9' },
        { name: '蓝色', value: '#409eff' },
        { name: '紫色', value: '#9c27b0' }
      ]
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
    selectColor(color) {
      this.currentColor = color
      this.$emit('select', `<span style="color: ${color}">`, '</span>')
      this.closePanel()
    },
    resetColor() {
      this.currentColor = ''
      this.$emit('reset')
      this.closePanel()
    }
  }
}
</script>

<style lang="scss" scoped>
.color-picker {
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

  .color-panel {
    position: absolute;
    bottom: calc(100% + 8px);
    left: 0;
    background: var(--card-bg);
    border-radius: $border-radius-lg;
    box-shadow: $shadow-lg;
    border: 1px solid var(--border-color);
    z-index: 1000;
    width: 200px;
    padding: $spacing-sm;

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

    .color-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: $spacing-xs;
      padding: $spacing-xs;

      .color-item {
        width: 32px;
        height: 32px;
        border: none;
        border-radius: $border-radius-sm;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.3s ease;

        i {
          color: white;
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
        }

        &:hover {
          transform: scale(1.1);
        }
      }
    }

    .panel-footer {
      border-top: 1px solid var(--border-color);
      margin-top: $spacing-xs;
      padding-top: $spacing-xs;

      .reset-btn {
        width: 100%;
        padding: $spacing-xs;
        border: none;
        background: none;
        color: var(--text-secondary);
        cursor: pointer;
        transition: all 0.3s ease;
        border-radius: $border-radius-sm;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: $spacing-xs;

        &:hover {
          color: $primary;
          background: var(--hover-bg);
        }
      }
    }
  }
}
</style> 