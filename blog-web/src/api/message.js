import request from '@/utils/request'

// 获取留言列表
export function getMessagesApi() {
  return request({
    url: '/api/message/list',
    method: 'get'
  })
}

// 发送留言
export function addMessageApi(data) {
  return request({
    url: '/api/message/add',
    method: 'post',
    data
  })
}
