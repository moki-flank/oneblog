import request from '@/utils/request'


/**
 * 获取标签列表
 * @returns 
 */
export function getTagsApi() {
    return request({
        url: '/api/tag/list',
        method: 'get'
    })
}

/**
 * 获取分类列表
 */
export function getCategoriesApi() {
    return request({
        url: '/sys/category/all',
        method: 'get'
    })
}

