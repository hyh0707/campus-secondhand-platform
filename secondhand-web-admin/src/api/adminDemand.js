import request from '../utils/request'

/**
 * 管理员求购列表（需管理员登录）
 * @param {Object} params - { pageNum, pageSize, status, keyword, categoryId }
 */
export function getAdminDemandList(params) {
  return request.get('/admin/demand/list', { params })
}

/**
 * 审核求购（需管理员登录）
 * @param {number} id - 求购ID
 * @param {Object} data - { status: 'approved'|'rejected', reason: string }
 */
export function auditDemand(id, data) {
  return request.put(`/admin/demand/audit/${id}`, data)
}
