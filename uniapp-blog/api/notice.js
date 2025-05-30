import request from '@/utils/request'

export function getNoticeApi() {
    return request({
        url: '/api/getNotice',
        method: 'get'
    })
}