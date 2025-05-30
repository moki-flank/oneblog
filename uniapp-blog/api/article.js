import request from '@/utils/request'

// 获取文章列表
export function getArticlesApi(params) {
    return request({
        url: '/api/article/list',
        method: 'get',
        data: params
    })
}

/**
 * 获取文章详情
 */
export function getArticleDetailApi(id) {
    return request({
      url: `/api/article/detail/${id}`,
      method: 'get'
    })
  }

// 获取轮播文章
export function getCarouselArticlesApi() {
  return request({
    url: '/api/article/getCarousels',
    method: 'get',
  })
}

// 获取所有分类
export function getCategoriesApi() {
  return request({
    url: '/api/article/categorie-all',
    method: 'get',
  })
}

// 点赞文章
export function likeArticleApi(id) {
  return request({
    url: `/api/article/like/${id}`,
    method: 'get'
  })
}

// 收藏文章
export function collectArticleApi(id) {
  return request({
    url: `/api/article/collect/${id}`,
    method: 'post'
  })
} 
