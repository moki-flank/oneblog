import request from '@/utils/request'

// 获取说说列表
export function loginApi(code) {
    return request({
        url: `/api/wechat/appletLogin/${code}`,
        method: 'get'
    })
}