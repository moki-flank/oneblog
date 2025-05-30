import request from '@/utils/request'

/**
 * 获取反馈表列表
 */
export function listSysFeedbackApi(params?: any) {
    return request({
        url: '/sys/feedback/list',
        method: 'get',
        params
    })
}

/**
 * 获取反馈表详情
 */
export function detailSysFeedbackApi(id: any) {
    return request({
        url: '/sys/feedback/' + id,
        method: 'get'
    })
}

/**
 * 添加反馈表
 */
export function addSysFeedbackApi(data: any) {
    return request({
        url: '/sys/feedback/add',
        method: 'post',
        data
    })
}

/**
 * 修改反馈表
 */
export function updateSysFeedbackApi(data: any) {
    return request({
        url: `/sys/feedback/update`,
        method: 'put',
        data
    })
}


/**
 * 删除反馈表
 */
export function deleteSysFeedbackApi(ids: number[] | number) {
    return request({
        url: `/sys/feedback/delete/` + ids,
        method: 'delete'
    })
}


