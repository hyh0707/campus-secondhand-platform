import request from '../utils/request'

/**
 * 管理员用户列表（需管理员登录）
 * @param {Object} params - { pageNum, pageSize, keyword, status }
 */
export function getAdminUserList(params) {
  return request.get('/admin/user/list', { params })
}

/**
 * 禁用/启用用户（需管理员登录）
 * @param {number} id - 用户ID
 * @param {Object} data - { status: 0|1 }
 */
export function updateUserStatus(id, data) {
  return request.put(`/admin/user/status/${id}`, data)
}
