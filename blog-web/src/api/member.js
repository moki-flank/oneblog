import request from '@/utils/request'

/**
 * 创建会员订单
 * @param {Object} data
 * @param {number} data.planId - 套餐ID
 * @param {string} data.paymentMethod - 支付方式：alipay/wechat
 */
export function createMemberOrder(data) {
  return request({
    url: '/api/member/order/create',
    method: 'post',
    data
  })
}

/**
 * 检查订单状态
 * @param {string} orderNo - 订单号
 */
export function checkOrderStatus(orderNo) {
  return request({
    url: `/api/member/order/status/${orderNo}`,
    method: 'get'
  })
}

/**
 * 获取会员信息
 */
export function getMemberInfo() {
  return request({
    url: '/api/member/info',
    method: 'get'
  })
}

/**
 * 获取会员套餐列表
 */
export function getMemberPlans() {
  return request({
    url: '/api/member/plans',
    method: 'get'
  })
} 