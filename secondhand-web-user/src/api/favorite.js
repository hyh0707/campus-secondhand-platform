import request from '../utils/request'

/**
 * 添加收藏
 * @param {number} goodsId - 商品ID
 */
export function addFavorite(goodsId) {
  return request.post('/favorite/add', { goodsId })
}

/**
 * 取消收藏
 * @param {number} goodsId - 商品ID
 */
export function deleteFavorite(goodsId) {
  return request.delete(`/favorite/delete/${goodsId}`)
}

/**
 * 获取收藏列表
 * @param {Object} params - { pageNum, pageSize }
 */
export function getFavoriteList(params) {
  return request.get('/favorite/list', { params })
}
