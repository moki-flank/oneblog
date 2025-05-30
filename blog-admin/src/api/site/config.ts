import request from '@/utils/request'

// 获取网站配置信息
export function getWebConfigApi() {
  return request({
    url: '/sys/web/config',
    method: 'get'
  })
}

// 保存网站配置信息
export function updateWebConfigApi(data: any) {
  return request({
    url: '/sys/web/update',
    method: 'put',
    data
  })
}