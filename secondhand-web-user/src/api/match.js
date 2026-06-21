import request from '../utils/request'

/**
 * 根据求购匹配商品（需登录）
 * @param {number} demandId - 求购ID
 * @param {Object} params - { limit: number }
 */
export function getGoodsForDemand(demandId, params) {
  return request.get(`/match/goods-for-demand/${demandId}`, { params })
}

/**
 * 根据商品匹配求购（需登录）
 * @param {number} goodsId - 商品ID
 * @param {Object} params - { limit: number }
 */
export function getDemandsForGoods(goodsId, params) {
  return request.get(`/match/demands-for-goods/${goodsId}`, { params })
}
