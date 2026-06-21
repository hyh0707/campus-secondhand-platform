import request from '../utils/request'

/**
 * 发布求购
 * @param {Object} data - { categoryId, title, description, minPrice, maxPrice, expectedCondition, expectedLocation, keywords }
 */
export function addDemand(data) {
  return request.post('/demand/add', data)
}

/**
 * 获取求购列表
 * @param {Object} params - { pageNum, pageSize, keyword, categoryId, minPrice, maxPrice, status }
 */
export function getDemandList(params) {
  return request.get('/demand/list', { params })
}

/**
 * 获取求购详情
 * @param {number} id - 求购ID
 */
export function getDemandDetail(id) {
  return request.get(`/demand/detail/${id}`)
}

/**
 * 获取我的求购列表
 * @param {Object} params - { pageNum, pageSize, status }
 */
export function getMyDemands(params) {
  return request.get('/demand/my', { params })
}

/**
 * 修改求购
 * @param {number} id - 求购ID
 * @param {Object} data - { categoryId, title, description, minPrice, maxPrice, expectedCondition, expectedLocation, keywords }
 */
export function updateDemand(id, data) {
  return request.put(`/demand/update/${id}`, data)
}

/**
 * 删除求购
 * @param {number} id - 求购ID
 */
export function deleteDemand(id) {
  return request.delete(`/demand/delete/${id}`)
}