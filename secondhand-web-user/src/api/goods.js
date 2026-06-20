import request from '../utils/request'

/**
 * 获取商品列表
 * @param {Object} params - { pageNum, pageSize, keyword, categoryId, minPrice, maxPrice, conditionLevel, status }
 */
export function getGoodsList(params) {
  return request.get('/goods/list', { params })
}

/**
 * 获取商品详情
 * @param {number} id - 商品ID
 */
export function getGoodsDetail(id) {
  return request.get(`/goods/detail/${id}`)
}

/**
 * 发布商品
 * @param {Object} data - { categoryId, title, description, price, conditionLevel, tradeLocation, contactInfo, negotiable, imageUrls }
 */
export function addGoods(data) {
  return request.post('/goods/add', data)
}

/**
 * 获取我的商品列表
 * @param {Object} params - { pageNum, pageSize, status }
 */
export function getMyGoods(params) {
  return request.get('/goods/my', { params })
}

/**
 * 修改商品
 * @param {number} id - 商品ID
 * @param {Object} data - { categoryId, title, description, price, conditionLevel, tradeLocation, contactInfo, negotiable, imageUrls }
 */
export function updateGoods(id, data) {
  return request.put(`/goods/update/${id}`, data)
}

/**
 * 删除商品
 * @param {number} id - 商品ID
 */
export function deleteGoods(id) {
  return request.delete(`/goods/delete/${id}`)
}