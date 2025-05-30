import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '@/views/home/index.vue'
import Layout from '@/layout/index.vue'
import NotFound from '@/views/404/404.vue'
import Article from '@/views/article/index.vue'
import Archive from '@/views/archives/index.vue'
import Categories from '@/views/categories/index.vue'
import Tags from '@/views/tags/index.vue'
import Messages from '@/views/messages/index.vue'
import About from '@/views/about/index.vue'
import Photos from '@/views/photos/index.vue'
import store from '@/store';

Vue.use(VueRouter)

const routes = [
    {
        path: "/",
        component: Layout,
        meta: {
            title: "拾壹博客-一个专注于技术分享的博客平台",
            loading: true
        },
        children: [
            {
                path: '/',
                name: 'Home',
                component: Home,
                meta: {
                    title: '首页',
                    transition: 'fade',
                    icon: 'fas fa-home',
                    loading: true
                 }
              },
              {
                path: '/archive',
                name: 'Archive',
                component: Archive,
                meta: { 
                  transition: 'fade',
                  title: '归档 - 拾壹博客',
                  icon: 'fas fa-archive'
                }
              },
              {
                path: '/categories',
                name: 'Categories',
                component: Categories,
                meta: {
                    transition: 'fade',
                    title: "分类 - 拾壹博客",
                    icon: 'fas fa-folder'
                 }
              },
              {
                path: '/tags',
                name: 'Tags',
                component: Tags,
                meta: {
                    transition: 'fade',
                    title: '标签 - 拾壹博客',
                    icon: 'fas fa-tags'
                }
              },
              {
                path: '/moments',
                name: 'Moments',
                component: () => import('@/views/moments/index.vue'),
                meta: {
                  title: '说说 - 拾壹博客',
                  icon: 'fas fa-comment-dots'
                }
              },
              {
                path: '/photos',
                name: 'Photos',
                component: Photos,
                meta: {
                    transition: 'fade',
                    title: '相册 - 拾壹博客',
                    icon: 'fas fa-images'
                }
              },
              {
                path: '/photos/:id',
                name: 'PhotoDetail',
                component: () => import('@/views/photos/detail.vue'),
                meta: {
                    transition: 'fade',
                    title: '相册详情 - 拾壹博客',
                    icon: 'fas fa-images',
                    hidden: true
                }
              },
              {
                path: '/hotSearch',
                name: 'HotSearch',
                component: () => import(/* webpackPrefetch: true */ '@/views/hotSearch/index2.vue'),
                meta: { 
                  transition: 'fade',
                  title: '热搜 - 拾壹博客',
                  icon: 'fas fa-fire'
                }
              },
              {
                path: '/messages',
                name: 'Messages',
                component: Messages,
                meta: { 
                  transition: 'fade',
                  title: '留言板 - 拾壹博客',
                  icon: 'fas fa-comments'
                }
              },
              {
                path: '/friends',
                name: 'Friends',
                component: () => import(/* webpackPrefetch: true */ '@/views/friends/index.vue'),
                meta: { 
                  transition: 'fade',
                  title: '友情链接 - 拾壹博客',
                  icon: 'fas fa-user-friends'
                }
              },
              {
                path: '/about',
                name: 'About',
                component: About,
                meta: { 
                  transition: 'fade',
                  title: '关于本站 - 拾壹博客',
                  icon: 'fas fa-info-circle'
                }
              },
              {
                path: '/post/:id',
                name: 'Post',
                component: Article,
                props: true,
                meta: {
                  hidden: true
                }
              },
      
              {
                path: '/user/profile',
                name: 'Profile',
                component: () => import(/* webpackPrefetch: true */ '@/views/profile/index.vue'),
                meta: {
                  title: '个人主页 - 拾壹博客',
                  icon: 'fas fa-user',
                  hidden: true
                }
              },
              {
                path: '/editor',
                name: 'Editor',
                component: () => import(/* webpackPrefetch: true */ '@/views/editor/index.vue'),
                meta: {
                  title: '写文章 - 拾壹博客',
                  icon: 'fas fa-edit',
                  requireAuth: true,
                  hidden: true
                }
              },
              {
                path: '/chat',
                name: 'Chat',
                component: () => import(/* webpackPrefetch: true */ '@/views/chat/index.vue'),
                meta: {
                  title: '聊天 - 拾壹博客',
                  icon: 'fas fa-comments',
                  hidden: true
                }
              },
              {
                path: '/:pathMatch(.*)*',
                name: 'NotFound',
                component: NotFound,
                meta: {
                  hidden: true
                }
              }
        ]
    }
]

const router = new VueRouter({
  mode: 'history',
  base: '/',
  routes,
  scrollBehavior(to, from, savedPosition) {
    return { x: 0, y: 0 }
  }
})


// 解决重复点击导航时，控制台出现报错
const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (to) {
  return VueRouterPush.call(this, to).catch(err => err)
}


router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title
  }
  //关闭搜索框
  store.commit('SET_SEARCH_VISIBLE', false)
  next()
})

export default router 