import request from '../utils/request'

/**
 * 个性化商品推荐（需登录）
 * @param {Object} params - { limit: number }
 */
export function getRecommendGoods(params) {
  return request.get('/recommend/goods', { params })
}

/**
 * 相似商品推荐（匿名可访问）
 * @param {number} goodsId - 商品ID
 * @param {Object} params - { limit: number }
 */
export function getSimilarGoods(goodsId, params) {
  return request.get(`/recommend/similar/${goodsId}`, { params })
}
