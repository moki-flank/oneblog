import request from '@/utils/request'

// 获取友链信息
export function getFriendListApi(params: any) {
  return request({
    url: '/sys/friend/list',
    method: 'get',
    params
  })
}

// 新增友链
export function addFriendApi(data: any) {
  return request({
    url: '/sys/friend/add',
    method: 'post',
    data
  })
}

// 修改友链
export function updateFriendApi(data: any) {
  return request({
    url: '/sys/friend/update',
    method: 'put',
    data
  })
}

// 删除友链
export function deleteFriendApi(ids: any) {
  return request({
    url: `/sys/friend/delete/${ids}`,
    method: 'delete'
  })
}