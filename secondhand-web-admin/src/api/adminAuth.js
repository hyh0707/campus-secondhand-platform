import request from '../utils/request'

/**
 * 管理员登录
 * @param {Object} data - { username, password }
 */
export function adminLogin(data) {
  return request.post('/admin/login', data)
}

/**
 * 获取当前管理员信息（需管理员登录）
 */
export function getAdminProfile() {
  return request.get('/admin/profile')
}
