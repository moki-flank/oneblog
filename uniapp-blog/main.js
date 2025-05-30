import Vue from 'vue'
import App from './App'
import uView from 'uview-ui'

// 导入导航栏组件
import navBar from '@/components/nav-bar/index.vue'

Vue.component('nav-bar', navBar)
Vue.use(uView)

// 如果项目中有使用到 $u 对象
Vue.prototype.$u = uView

// 引入请求
import request from './utils/request'
Vue.prototype.$request = request

// 生产环境关闭提示
Vue.config.productionTip = false

// 调试模式
Vue.config.debug = process.env.NODE_ENV !== 'production'

App.mpType = 'app'

const app = new Vue({
  ...App
})
app.$mount() 