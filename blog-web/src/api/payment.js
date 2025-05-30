import request from '@/utils/request'

/**
 * 创建支付订单
 * @param {Object} data
 * @param {string} data.articleId - 文章ID
 * @param {string} data.paymentMethod - 支付方式：alipay/wechat
 * @param {number} data.amount - 支付金额
 */
export function createOrder(data) {
  return request({
    url: '/api/payment/create',
    method: 'post',
    data
  })
}

/**
 * 检查支付订单状态
 * @param {string} orderNo - 订单号
 */
export function checkOrderStatus(orderNo) {
  return request({
    url: `/api/payment/status/${orderNo}`,
    method: 'get'
  })
}

/**
 * 获取用户支付记录
 * @param {Object} params
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页数量
 */
export function getPaymentHistory(params) {
  return request({
    url: '/api/payment/history',
    method: 'get',
    params
  })
} 