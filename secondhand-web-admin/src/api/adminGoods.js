import request from '../utils/request'

/**
 * 管理员商品列表（需管理员登录）
 * @param {Object} params - { pageNum, pageSize, status, keyword, categoryId }
 */
export function getAdminGoodsList(params) {
  return request.get('/admin/goods/list', { params })
}

/**
 * 审核商品（需管理员登录）
 * @param {number} id - 商品ID
 * @param {Object} data - { status: 'approved'|'rejected', reason: string }
 */
export function auditGoods(id, data) {
  return request.put(`/admin/goods/audit/${id}`, data)
}

/**
 * 下架商品（需管理员登录）
 * @param {number} id - 商品ID
 * @param {Object} data - { reason: string }
 */
export function offlineGoods(id, data) {
  return request.put(`/admin/goods/offline/${id}`, data)
}
