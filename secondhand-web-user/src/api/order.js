import request from '../utils/request'

/**
 * 创建交易意向
 * @param {Object} data - { goodsId, message }
 */
export function createOrder(data) {
  return request.post('/order/create', data)
}

/**
 * 更新交易状态
 * @param {number} id - 交易意向ID
 * @param {string} status - 新状态
 */
export function updateOrderStatus(id, status) {
  return request.put(`/order/status/${id}`, { status })
}

/**
 * 获取我的购买意向
 * @param {Object} params - { pageNum, pageSize, status }
 */
export function getMyBuyOrders(params) {
  return request.get('/order/my-buy', { params })
}

/**
 * 获取我的出售意向
 * @param {Object} params - { pageNum, pageSize, status }
 */
export function getMySellOrders(params) {
  return request.get('/order/my-sell', { params })
}
