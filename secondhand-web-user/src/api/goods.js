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