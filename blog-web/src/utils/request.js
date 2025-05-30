import axios from 'axios'
import { getToken,removeToken } from '@/utils/cookie'
import store from '@/store'

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 可以在这里添加请求头等配置
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else if(res.code === 404){
      return Promise.reject(new Error('请求路径不存在'))
    }else if(res.code === 401){
      removeToken()
      //这里获取不到this，所以需要使用全局变量
      store.commit('SET_USER_INFO', null)
      store.commit('SET_LOGIN_VISIBLE', true)
      return Promise.reject(new Error('当前登录已过期，请重新登录'))
    }else {
      // 可以在这里统一处理错误
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    return Promise.reject(error)
  }
)

export default service