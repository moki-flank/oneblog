<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="transition-width">
      <Sidebar :is-collapse="isCollapse" />
    </el-aside>
    <el-container>
      <el-header class="header">
        <Navbar 
          :is-collapse="isCollapse"
          @toggle-collapse="toggleCollapse"
          @lock="handleLock"
          @theme-click="drawerVisible = true"
        />
      </el-header>
      <!-- 标签页 -->
      <tags-view v-if="settingsStore.showTags" />

      <el-main class="main-container">
 
        <router-view v-slot="{ Component }">
          <transition 
            :name="settingsStore.pageAnimation" 
            mode="out-in"
            appear
          >
            <keep-alive :include="cachedViews">
              <component 
                :is="Component" 
                :key="$route.fullPath" 
              />
            </keep-alive>
          </transition>

        </router-view>

      </el-main>

      <!-- 添加页脚 -->
      <Footer v-if="settingsStore.showFooter" />
    </el-container>
  </el-container>

  <!-- 设置抽屉 -->
  <setting-drawer
    v-model:visible="drawerVisible"
    v-model:isCollapse="isCollapse"
  />

  <!-- 添加锁屏组件 -->
  <lock-screen ref="lockScreenRef" />

  <!-- 水印组件 -->
  <Watermark />
</template>

<script setup lang="ts">
import TagsView from '@/components/TagsView/index.vue'
import SettingDrawer from '@/components/SettingDrawer/index.vue'
import Navbar from '@/layouts/components/Navbar/index.vue'
import Sidebar from './components/Sidebar/index.vue'
import LockScreen from '@/components/LockScreen/index.vue'
import Watermark from '@/components/Watermark/index.vue'
import Footer from '@/components/Footer/index.vue'

import { useSettingsStore, usePermissionStore } from "@/store";
import { useTagsViewStore } from '@/store/modules/tagsView'

const route = useRoute()
const tagsViewStore = useTagsViewStore()

const isCollapse = ref<boolean>(false)

const activeMenu = computed(() => route.path)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const drawerVisible = ref(false)

const settingsStore = useSettingsStore()

// 缓存的视图
const cachedViews = computed(() => tagsViewStore.cachedViews)

// 初始化固定标签
onMounted(() => {
  tagsViewStore.initTags()
})

const lockScreenRef = ref()

const handleLock = () => {
  lockScreenRef.value?.lock()
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
  background-color: var(--el-bg-color);
  color: var(--el-text-color-primary);
}

.transition-width {
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.el-aside {
  background-color: #304156;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: var(--el-bg-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.main-container {
  padding: 16px;
  overflow-y: auto;
  background-color: var(--el-bg-color);
}
</style> 