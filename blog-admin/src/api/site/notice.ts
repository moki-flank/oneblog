import request from '@/utils/request'

/**
 * 获取公告列表
 */
export function listSysNoticeApi(params?: any) {
    return request({
        url: '/sys/notice/list',
        method: 'get',
        params
    })
}

/**
 * 获取公告详情
 */
export function detailSysNoticeApi(id: any) {
    return request({
        url: '/sys/notice/' + id,
        method: 'get'
    })
}

/**
 * 添加公告
 */
export function addSysNoticeApi(data: any) {
    return request({
        url: '/sys/notice/add',
        method: 'post',
        data
    })
}

/**
 * 修改公告
 */
export function updateSysNoticeApi(data: any) {
    return request({
        url: `/sys/notice/update`,
        method: 'put',
        data
    })
}


/**
 * 删除公告
 */
export function deleteSysNoticeApi(ids: number[] | number) {
    return request({
        url: `/sys/notice/delete/` + ids,
        method: 'delete'
    })
}


