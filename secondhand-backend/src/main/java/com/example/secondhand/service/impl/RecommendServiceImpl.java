package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.secondhand.common.BusinessException;
import com.example.secondhand.common.ResultCode;
import com.example.secondhand.entity.*;
import com.example.secondhand.mapper.*;
import com.example.secondhand.service.RecommendService;
import com.example.secondhand.utils.KeywordUtils;
import com.example.secondhand.utils.MatchAlgorithmUtils;
import com.example.secondhand.vo.RecommendGoodsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    private final GoodsMapper goodsMapper;
    private final GoodsImageMapper goodsImageMapper;
    private final UserMapper userMapper;
    private final SearchHistoryMapper searchHistoryMapper;
    private final BrowseHistoryMapper browseHistoryMapper;
    private final FavoriteMapper favoriteMapper;
    private final DemandMapper demandMapper;

    @Override
    public List<RecommendGoodsVO> recommendGoods(Long userId, Integer limit) {
        if (limit == null || limit <= 0) limit = 10;

        // 收集用户画像数据
        // 1. 搜索历史关键词
        List<SearchHistory> searchHistories = searchHistoryMapper.selectList(
                new LambdaQueryWrapper<SearchHistory>()
                        .eq(SearchHistory::getUserId, userId)
                        .orderByDesc(SearchHistory::getCreateTime)
                        .last("LIMIT 20"));
        Set<String> searchKeywords = searchHistories.stream()
                .map(SearchHistory::getKeyword)
                .collect(Collectors.toSet());

        // 2. 浏览历史商品 ID
        List<BrowseHistory> browseHistories = browseHistoryMapper.selectList(
                new LambdaQueryWrapper<BrowseHistory>()
                        .eq(BrowseHistory::getUserId, userId)
                        .orderByDesc(BrowseHistory::getCreateTime)
                        .last("LIMIT 20"));
        Set<Long> browsedGoodsIds = browseHistories.stream()
                .map(BrowseHistory::getGoodsId)
                .collect(Collectors.toSet());

        // 3. 收藏商品 ID
        List<Favorite> favorites = favoriteMapper.selectList(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreateTime)
                        .last("LIMIT 20"));
        Set<Long> favoriteGoodsIds = favorites.stream()
                .map(Favorite::getGoodsId)
                .collect(Collectors.toSet());

        // 4. 用户发布的求购关键词
        List<Demand> userDemands = demandMapper.selectList(
                new LambdaQueryWrapper<Demand>()
                        .eq(Demand::getUserId, userId)
                        .eq(Demand::getStatus, "approved"));
        Set<String> demandKeywords = new HashSet<>();
        for (Demand d : userDemands) {
            demandKeywords.addAll(KeywordUtils.extractKeywords(
                    d.getTitle(), d.getDescription(), d.getKeywords()));
        }

        // 汇总关键词
        Set<String> allInterestKeywords = new HashSet<>();
        allInterestKeywords.addAll(searchKeywords);
        allInterestKeywords.addAll(demandKeywords);

        // 浏览和收藏的商品 ID 集合
        Set<Long> interestedGoodsIds = new HashSet<>();
        interestedGoodsIds.addAll(browsedGoodsIds);
        interestedGoodsIds.addAll(favoriteGoodsIds);

        boolean hasBehaviorData = !allInterestKeywords.isEmpty() || !interestedGoodsIds.isEmpty();

        // 查所有 approved 商品（排除自己发布的）
        List<Goods> allGoods = goodsMapper.selectList(
                new LambdaQueryWrapper<Goods>()
                        .eq(Goods::getStatus, "approved")
                        .ne(Goods::getUserId, userId));

        // 计算推荐分数
        List<GoodsWithScore> scoredGoods = new ArrayList<>();
        for (Goods g : allGoods) {
            int score;
            List<String> reasons = new ArrayList<>();

            if (hasBehaviorData) {
                // 基于用户画像评分
                Set<String> goodsKeywords = KeywordUtils.extractKeywords(
                        g.getTitle(), g.getDescription(), null);

                double keywordJaccard = KeywordUtils.jaccardSimilarity(goodsKeywords, allInterestKeywords);
                score = (int) Math.round(keywordJaccard * 60);

                if (interestedGoodsIds.contains(g.getId())) {
                    score += 20;
                    reasons.add("浏览/收藏");
                }

                if (keywordJaccard > 0.1) {
                    reasons.add("兴趣匹配(" + Math.round(keywordJaccard * 100) + "%)");
                }

                // 热度加成
                int heat = (g.getViewCount() != null ? g.getViewCount() : 0)
                        + (g.getFavoriteCount() != null ? g.getFavoriteCount() * 2 : 0);
                if (heat >= 20) {
                    score += 10;
                    reasons.add("热门商品");
                } else if (heat >= 5) {
                    score += 5;
                }
            } else {
                // 无行为数据：热门商品推荐
                int viewCount = g.getViewCount() != null ? g.getViewCount() : 0;
                int favCount = g.getFavoriteCount() != null ? g.getFavoriteCount() : 0;
                score = viewCount + favCount * 3;
                if (score > 0) reasons.add("热门商品");
            }

            scoredGoods.add(new GoodsWithScore(g, score, reasons));
        }

        // 排序取前 limit
        scoredGoods.sort((a, b) -> Integer.compare(b.score, a.score));
        int actualLimit = Math.min(limit, scoredGoods.size());

        return buildResult(scoredGoods.subList(0, actualLimit));
    }

    @Override
    public List<RecommendGoodsVO> recommendSimilar(Long goodsId, Integer limit) {
        Goods target = goodsMapper.selectById(goodsId);
        if (target == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (limit == null || limit <= 0) limit = 10;

        Set<String> targetKeywords = KeywordUtils.extractKeywords(
                target.getTitle(), target.getDescription(), null);

        // 查所有 approved 商品（排除自己）
        List<Goods> allGoods = goodsMapper.selectList(
                new LambdaQueryWrapper<Goods>()
                        .eq(Goods::getStatus, "approved")
                        .ne(Goods::getId, goodsId));

        List<GoodsWithScore> scoredGoods = new ArrayList<>();
        for (Goods g : allGoods) {
            Set<String> goodsKeywords = KeywordUtils.extractKeywords(
                    g.getTitle(), g.getDescription(), null);

            MatchAlgorithmUtils.MatchResult mr = MatchAlgorithmUtils.calculateSimilarityScore(
                    g.getCategoryId(), target.getCategoryId(),
                    goodsKeywords, targetKeywords,
                    g.getPrice(), target.getPrice(),
                    g.getConditionLevel(), target.getConditionLevel());

            scoredGoods.add(new GoodsWithScore(g, mr.score, mr.reasons));
        }

        scoredGoods.sort((a, b) -> Integer.compare(b.score, a.score));
        int actualLimit = Math.min(limit, scoredGoods.size());

        return buildResult(scoredGoods.subList(0, actualLimit));
    }

    private List<RecommendGoodsVO> buildResult(List<GoodsWithScore> scoredGoods) {
        if (scoredGoods.isEmpty()) return Collections.emptyList();

        Set<Long> goodsIdSet = scoredGoods.stream()
                .map(gs -> gs.goods.getId())
                .collect(Collectors.toSet());
        Set<Long> userIdSet = scoredGoods.stream()
                .map(gs -> gs.goods.getUserId())
                .collect(Collectors.toSet());

        // 批量查主图
        final Map<Long, String> mainImageMap = new HashMap<>();
        if (!goodsIdSet.isEmpty()) {
            List<GoodsImage> images = goodsImageMapper.selectList(
                    new LambdaQueryWrapper<GoodsImage>()
                            .in(GoodsImage::getGoodsId, goodsIdSet)
                            .orderByAsc(GoodsImage::getSort));
            for (GoodsImage img : images) {
                mainImageMap.putIfAbsent(img.getGoodsId(), img.getImageUrl());
            }
        }

        // 批量查用户
        final Map<Long, User> userMap;
        if (userIdSet.isEmpty()) {
            userMap = Collections.emptyMap();
        } else {
            userMap = userMapper.selectBatchIds(userIdSet).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        }

        return scoredGoods.stream().map(gs -> {
            RecommendGoodsVO vo = new RecommendGoodsVO();
            vo.setGoodsId(gs.goods.getId());
            vo.setTitle(gs.goods.getTitle());
            vo.setPrice(gs.goods.getPrice());
            vo.setConditionLevel(gs.goods.getConditionLevel());
            vo.setMainImage(mainImageMap.get(gs.goods.getId()));
            vo.setRecommendScore(gs.score);
            vo.setRecommendReasons(gs.reasons);

            User seller = userMap.get(gs.goods.getUserId());
            if (seller != null) {
                vo.setSellerNickname(seller.getNickname() != null ? seller.getNickname() : seller.getUsername());
            }

            return vo;
        }).collect(Collectors.toList());
    }

    private static class GoodsWithScore {
        final Goods goods;
        final int score;
        final List<String> reasons;

        GoodsWithScore(Goods goods, int score, List<String> reasons) {
            this.goods = goods;
            this.score = score;
            this.reasons = reasons;
        }
    }
}