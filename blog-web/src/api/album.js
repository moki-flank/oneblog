import request from '@/utils/request'

/**
 * 获取相册列表
 */
export function getAlbumListApi() {
  return request({
    url: '/api/album/list',
    method: 'get'
  })
}

/**
 * 获取相册详情
 */
export function getAlbumDetailApi(id) {
  return request({
    url: `/api/album/detail/${id}`,
    method: 'get'
  })
}

/**
 * 获取相册照片
 */
export function getAlbumPhotosApi(id) {
  return request({
    url: `/api/album/photos/${id}`,
    method: 'get'
  })
}

/**
 * 验证相册密码
 */
export function verifyAlbumPasswordApi(id, password) {
  return request({
    url: `/api/album/verify/${id}`,
    method: 'get',
    params: {
      password: password
    }
  })
}