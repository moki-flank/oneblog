import request from '@/utils/request'

// 获取分类信息
export function getCategoryListApi(params: any) {
  return request({
    url: '/sys/category/list',
    method: 'get',
    params
  })
}

// 新增分类
export function addCategoryApi(data: any) {
  return request({
    url: '/sys/category',
    method: 'post',
    data
  })
}

// 修改分类
export function updateCategoryApi(data: any) {
  return request({
    url: '/sys/category',
    method: 'put',
    data
  })
}

// 删除分类
export function deleteCategoryApi(ids: any) {
  return request({
    url: `/sys/category/delete/${ids}`,
    method: 'delete'
  })
}