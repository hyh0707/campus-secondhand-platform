package com.example.secondhand.service;

import com.example.secondhand.vo.RecommendGoodsVO;

import java.util.List;

public interface RecommendService {

    /**
     * 个性化商品推荐
     */
    List<RecommendGoodsVO> recommendGoods(Long userId, Integer limit);

    /**
     * 相似商品推荐（匿名可访问）
     */
    List<RecommendGoodsVO> recommendSimilar(Long goodsId, Integer limit);
}