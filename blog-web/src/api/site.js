import request from '@/utils/request'


export function getWebConfigApi() {
    return request({
        url: '/api/webConfig',
        method: 'get'
    })
}


export function getNoticeApi() {
    return request({
        url: '/api/getNotice',
        method: 'get'
    })
}
export function reportApi() {
    return request({
        url: '/api/report',
        method: 'get'
    })
}