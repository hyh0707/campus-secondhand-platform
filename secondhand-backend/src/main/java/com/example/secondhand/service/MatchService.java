package com.example.secondhand.service;

import com.example.secondhand.vo.DemandMatchVO;
import com.example.secondhand.vo.GoodsMatchVO;

import java.util.List;

public interface MatchService {

    /**
     * 根据求购匹配商品
     */
    List<GoodsMatchVO> matchGoodsForDemand(Long demandId, Long userId, Integer limit);

    /**
     * 根据商品匹配求购
     */
    List<DemandMatchVO> matchDemandsForGoods(Long goodsId, Long userId, Integer limit);
}