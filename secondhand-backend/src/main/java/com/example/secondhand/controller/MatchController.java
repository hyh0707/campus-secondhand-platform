package com.example.secondhand.controller;

import com.example.secondhand.common.Result;
import com.example.secondhand.service.MatchService;
import com.example.secondhand.service.RecommendService;
import com.example.secondhand.utils.UserContext;
import com.example.secondhand.vo.DemandMatchVO;
import com.example.secondhand.vo.GoodsMatchVO;
import com.example.secondhand.vo.RecommendGoodsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final RecommendService recommendService;

    /**
     * 根据求购匹配商品（需登录）
     */
    @GetMapping("/match/goods-for-demand/{demandId}")
    public Result<List<GoodsMatchVO>> matchGoodsForDemand(
            @PathVariable Long demandId,
            @RequestParam(defaultValue = "10") Integer limit) {
        Long userId = UserContext.getUserId();
        List<GoodsMatchVO> list = matchService.matchGoodsForDemand(demandId, userId, limit);
        return Result.success(list);
    }

    /**
     * 根据商品匹配求购（需登录）
     */
    @GetMapping("/match/demands-for-goods/{goodsId}")
    public Result<List<DemandMatchVO>> matchDemandsForGoods(
            @PathVariable Long goodsId,
            @RequestParam(defaultValue = "10") Integer limit) {
        Long userId = UserContext.getUserId();
        List<DemandMatchVO> list = matchService.matchDemandsForGoods(goodsId, userId, limit);
        return Result.success(list);
    }

    /**
     * 个性化商品推荐（需登录）
     */
    @GetMapping("/recommend/goods")
    public Result<List<RecommendGoodsVO>> recommendGoods(
            @RequestParam(defaultValue = "10") Integer limit) {
        Long userId = UserContext.getUserId();
        List<RecommendGoodsVO> list = recommendService.recommendGoods(userId, limit);
        return Result.success(list);
    }

    /**
     * 相似商品推荐（匿名可访问）
     */
    @GetMapping("/recommend/similar/{goodsId}")
    public Result<List<RecommendGoodsVO>> recommendSimilar(
            @PathVariable Long goodsId,
            @RequestParam(defaultValue = "10") Integer limit) {
        List<RecommendGoodsVO> list = recommendService.recommendSimilar(goodsId, limit);
        return Result.success(list);
    }
}