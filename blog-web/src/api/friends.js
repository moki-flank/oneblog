import request from '@/utils/request'


/**
 * 获取友链列表
 * @returns 
 */
export function getFriendsApi() {
    return request({
        url: '/api/friend/list',
        method: 'get'
    })
}

/**
 * 申请友链
 * @returns 
 */
export function applyFriendApi(data) {
    return request({
        url: '/api/friend/apply',
        method: 'post',
        data
    })
}
