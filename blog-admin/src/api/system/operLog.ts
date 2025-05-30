import request from '@/utils/request'
import type { OperationLog, LogQueryParams, PageResult } from '@/types/log'

// 获取操作日志列表
export function getOperationLogsApi(params: any) {
  return request<any>({
    url: '/sys/operateLog',
    method: 'get',
    params
  })
}

// 批量删除操作日志
export function deleteOperationLogsApi(ids: any) {
  return request({
    url: `/sys/operateLog/delete/${ids}`,
    method: 'delete'
  })
}
