import request from '../utils/request'

/**
 * 获取平台统计概览（需管理员登录）
 */
export function getStatisticsOverview() {
  return request.get('/admin/statistics/overview')
}
