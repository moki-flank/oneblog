import request from '@/utils/request'

export function sendMsg(data) {
  return request({
    url: `/chat/sendMsg`,
    method: 'post',
    data
  })
} 

export function recallMsgApi(data) {
  return request({
    url: `/chat/recallMsg`,
    method: 'post',
    data
  })
} 
export function getChatMsgListApi(params) {
  return request({
    url: `/chat/list`,
    method: 'get',
    params
  })
} 