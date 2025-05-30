import request from '@/utils/request'

// 获取说说列表
export function getMomentsApi(params) {
    return request({
        url: '/api/moment/list',
        method: 'get',
        data: params
    })
}