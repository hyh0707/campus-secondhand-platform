import request from '../utils/request'

/**
 * 管理员订单列表（需管理员登录）
 * @param {Object} params - { pageNum, pageSize, status, buyerId, sellerId, goodsId }
 */
export function getAdminOrderList(params) {
  return request.get('/admin/order/list', { params })
}
