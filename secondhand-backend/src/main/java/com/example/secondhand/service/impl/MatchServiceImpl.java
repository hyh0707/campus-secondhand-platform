package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.secondhand.common.BusinessException;
import com.example.secondhand.common.ResultCode;
import com.example.secondhand.entity.Demand;
import com.example.secondhand.entity.Goods;
import com.example.secondhand.entity.GoodsImage;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.DemandMapper;
import com.example.secondhand.mapper.GoodsImageMapper;
import com.example.secondhand.mapper.GoodsMapper;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.service.MatchService;
import com.example.secondhand.utils.KeywordUtils;
import com.example.secondhand.utils.MatchAlgorithmUtils;
import com.example.secondhand.vo.DemandMatchVO;
import com.example.secondhand.vo.GoodsMatchVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final DemandMapper demandMapper;
    private final GoodsMapper goodsMapper;
    private final GoodsImageMapper goodsImageMapper;
    private final UserMapper userMapper;

    @Override
    public List<GoodsMatchVO> matchGoodsForDemand(Long demandId, Long userId, Integer limit) {
        Demand demand = demandMapper.selectById(demandId);
        if (demand == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!demand.getUserId().equals(userId)) {
            throw new BusinessException("只能对自己的求购进行匹配");
        }

        if (limit == null || limit <= 0) limit = 10;

        Set<String> demandKeywords = KeywordUtils.extractKeywords(
                demand.getTitle(), demand.getDescription(), demand.getKeywords());

        // 只查 approved 商品，且不是自己发布的
        List<Goods> allGoods = goodsMapper.selectList(
                new LambdaQueryWrapper<Goods>()
                        .eq(Goods::getStatus, "approved")
                        .ne(Goods::getUserId, userId));

        // 计算匹配分数
        List<MatchAlgorithmUtils.MatchResult> matchResults = new ArrayList<>();
        Map<Long, Goods> goodsMap = new HashMap<>();
        for (Goods g : allGoods) {
            Set<String> goodsKeywords = KeywordUtils.extractKeywords(
                    g.getTitle(), g.getDescription(), null);
            MatchAlgorithmUtils.MatchResult mr = MatchAlgorithmUtils.calculateGoodsDemandMatch(
                    g.getCategoryId(), demand.getCategoryId(),
                    goodsKeywords, demandKeywords,
                    g.getPrice(), demand.getMinPrice(), demand.getMaxPrice(),
                    g.getConditionLevel(), demand.getExpectedCondition(),
                    g.getTradeLocation(), demand.getExpectedLocation(),
                    g.getViewCount() != null ? g.getViewCount() : 0,
                    g.getFavoriteCount() != null ? g.getFavoriteCount() : 0);
            matchResults.add(mr);
            goodsMap.put(g.getId(), g);
        }

        // 按分数降序，取前 limit
        List<Integer> sortedIndices = new ArrayList<>();
        for (int i = 0; i < allGoods.size(); i++) sortedIndices.add(i);
        sortedIndices.sort((a, b) -> Integer.compare(matchResults.get(b).score, matchResults.get(a).score));

        int actualLimit = Math.min(limit, sortedIndices.size());

        // 收集需要查询的 goodsId 和 userId
        Set<Long> goodsIdSet = new HashSet<>();
        Set<Long> userIdSet = new HashSet<>();
        for (int i = 0; i < actualLimit; i++) {
            Goods g = allGoods.get(sortedIndices.get(i));
            goodsIdSet.add(g.getId());
            userIdSet.add(g.getUserId());
        }

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

        // 组装 VO
        List<GoodsMatchVO> result = new ArrayList<>();
        for (int i = 0; i < actualLimit; i++) {
            Goods g = allGoods.get(sortedIndices.get(i));
            MatchAlgorithmUtils.MatchResult mr = matchResults.get(sortedIndices.get(i));

            GoodsMatchVO vo = new GoodsMatchVO();
            vo.setGoodsId(g.getId());
            vo.setTitle(g.getTitle());
            vo.setPrice(g.getPrice());
            vo.setConditionLevel(g.getConditionLevel());
            vo.setMainImage(mainImageMap.get(g.getId()));
            vo.setMatchScore(mr.score);
            vo.setMatchReasons(mr.reasons);

            User seller = userMap.get(g.getUserId());
            if (seller != null) {
                vo.setSellerNickname(seller.getNickname() != null ? seller.getNickname() : seller.getUsername());
            }

            result.add(vo);
        }

        return result;
    }

    @Override
    public List<DemandMatchVO> matchDemandsForGoods(Long goodsId, Long userId, Integer limit) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!goods.getUserId().equals(userId)) {
            throw new BusinessException("只能对自己发布的商品查看匹配求购");
        }

        if (limit == null || limit <= 0) limit = 10;

        Set<String> goodsKeywords = KeywordUtils.extractKeywords(
                goods.getTitle(), goods.getDescription(), null);

        // 只查 approved 求购
        List<Demand> allDemands = demandMapper.selectList(
                new LambdaQueryWrapper<Demand>()
                        .eq(Demand::getStatus, "approved"));

        // 计算匹配分数
        List<MatchAlgorithmUtils.MatchResult> matchResults = new ArrayList<>();
        for (Demand d : allDemands) {
            Set<String> demandKeywords = KeywordUtils.extractKeywords(
                    d.getTitle(), d.getDescription(), d.getKeywords());
            MatchAlgorithmUtils.MatchResult mr = MatchAlgorithmUtils.calculateGoodsDemandMatch(
                    goods.getCategoryId(), d.getCategoryId(),
                    goodsKeywords, demandKeywords,
                    goods.getPrice(), d.getMinPrice(), d.getMaxPrice(),
                    goods.getConditionLevel(), d.getExpectedCondition(),
                    goods.getTradeLocation(), d.getExpectedLocation(),
                    goods.getViewCount() != null ? goods.getViewCount() : 0,
                    goods.getFavoriteCount() != null ? goods.getFavoriteCount() : 0);
            matchResults.add(mr);
        }

        // 排序取前 limit
        List<Integer> sortedIndices = new ArrayList<>();
        for (int i = 0; i < allDemands.size(); i++) sortedIndices.add(i);
        sortedIndices.sort((a, b) -> Integer.compare(matchResults.get(b).score, matchResults.get(a).score));

        int actualLimit = Math.min(limit, sortedIndices.size());

        // 批量查用户
        Set<Long> userIdSet = new HashSet<>();
        for (int i = 0; i < actualLimit; i++) {
            userIdSet.add(allDemands.get(sortedIndices.get(i)).getUserId());
        }
        final Map<Long, User> userMap;
        if (userIdSet.isEmpty()) {
            userMap = Collections.emptyMap();
        } else {
            userMap = userMapper.selectBatchIds(userIdSet).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        }

        // 组装 VO
        List<DemandMatchVO> result = new ArrayList<>();
        for (int i = 0; i < actualLimit; i++) {
            Demand d = allDemands.get(sortedIndices.get(i));
            MatchAlgorithmUtils.MatchResult mr = matchResults.get(sortedIndices.get(i));

            DemandMatchVO vo = new DemandMatchVO();
            vo.setDemandId(d.getId());
            vo.setTitle(d.getTitle());
            vo.setMinPrice(d.getMinPrice());
            vo.setMaxPrice(d.getMaxPrice());
            vo.setExpectedCondition(d.getExpectedCondition());
            vo.setExpectedLocation(d.getExpectedLocation());
            vo.setMatchScore(mr.score);
            vo.setMatchReasons(mr.reasons);

            User u = userMap.get(d.getUserId());
            if (u != null) {
                vo.setUserNickname(u.getNickname() != null ? u.getNickname() : u.getUsername());
            }

            result.add(vo);
        }

        return result;
    }
}