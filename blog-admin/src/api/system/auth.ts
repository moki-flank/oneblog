import request from '@/utils/request'

interface LoginParams {
  username: string
  password: string
  captchaCode: string
  captchaKey: string
  rememberMe: boolean
}

// 登录接口
export function loginApi(data: LoginParams) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function logoutApi() {
    return request({
      url: '/auth/logout',
      method: 'post',
    })
  }

// 获取用户信息
export function getUserInfoApi() {
  return request({
    url: "/auth/info",
    method: "get"
  })
}

export function getRouters() {
  return request({
    url: '/sys/menu/routers',
    method: 'get'
  })
}
