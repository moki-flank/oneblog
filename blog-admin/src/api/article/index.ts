import request from '@/utils/request'

// 获取文章列表
export function getArticleListApi(params: any) {
  return request({
    url: '/sys/article/list',
    method: 'get',
    params
  })
}

// 获取文章详情
export function getDetailApi(id: any) {
  return request({
    url: `/sys/article/detail/${id}`,
    method: 'get',
  })
}

// 新增文章
export function addArticleApi(data: any) {
  return request({
    url: '/sys/article/add',
    method: 'post',
    data
  })
}

// 修改文章
export function updateArticleApi(data: any) {
  return request({
    url: '/sys/article/update',
    method: 'put',
    data
  })
}


// 修改文章状态
export function updateStatusApi(data: any) {
  return request({
    url: '/sys/article/updateStatus',
    method: 'put',
    data
  })
}
// 删除文章
export function deleteArticleApi(ids: any) {
  return request({
    url: `/sys/article/delete/${ids}`,
    method: 'delete'
  })
}

// 爬取文章
export function reptileArticleApi(url: any) {
  return request({
    url: '/sys/article/reptile',
    method: 'get',
    params: {url: url}
  })
}