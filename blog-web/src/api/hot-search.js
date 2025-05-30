import request from '@/utils/request'

export function getHotListApi(type) {
  return request({
    url: `/api/getHotSearch/${type}`,
    method: 'get'
  })
} 