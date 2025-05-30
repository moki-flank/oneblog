import request from '@/utils/request'

/**
 * 登录
 * @param {*} data 
 * @returns 
 */
export function loginApi(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 退出登录
 * @returns 
 */
export function logoutApi() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 获取用户信息
 */
export function getUserInfoApi() {
  return request({
    url: `/auth/info`,
    method: 'get',
  })
} 

/**
 * 发送邮箱验证吗
 */
export function sendEmailCodeApi(email) {
  return request({
    url: `/api/sendEmailCode`,
    method: 'get',
    params:{
        email:email
    }
  })
} 

/**
 * 注册
 */
export function registerApi(data) {
  return request({
    url: `/api/email/register`,
    method: 'post',
    data
  })
}

/**
 * 忘记密码
 */
export function forgotPasswordApi(data) {
    return request({
      url: `/api/email/forgot`,
      method: 'post',
      data
    })
  }

/**
 * 获取微信登录验证码
 */
export function getWechatLoginCodeApi() {
  return request({
    url: `/api/wechat/getCode`,
    method: 'get',
  })
} 

/**
 * 获取微信登录状态
 */
export function getWechatIsLoginApi(code) {
  return request({
    url: `/api/wechat/isLogin/${code}`,
    method: 'get',
  })
} 

/**
 * 获取第三方授权地址
 */
export function getAuthRenderApi(source) {
  return request({
    url: `/api/auth/render/${source}`,
    method: 'get',
  })
} 