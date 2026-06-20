package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.secondhand.dto.AdminOrderQueryDTO;
import com.example.secondhand.entity.Goods;
import com.example.secondhand.entity.TradeOrder;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.GoodsMapper;
import com.example.secondhand.mapper.TradeOrderMapper;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.service.AdminOrderService;
import com.example.secondhand.vo.AdminOrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final TradeOrderMapper tradeOrderMapper;
    private final GoodsMapper goodsMapper;
    private final UserMapper userMapper;

    @Override
    public Page<AdminOrderVO> listOrders(AdminOrderQueryDTO query) {
        LambdaQueryWrapper<TradeOrder> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(TradeOrder::getStatus, query.getStatus());
        }
        if (query.getBuyerId() != null) {
            wrapper.eq(TradeOrder::getBuyerId, query.getBuyerId());
        }
        if (query.getSellerId() != null) {
            wrapper.eq(TradeOrder::getSellerId, query.getSellerId());
        }
        if (query.getGoodsId() != null) {
            wrapper.eq(TradeOrder::getGoodsId, query.getGoodsId());
        }

        wrapper.orderByDesc(TradeOrder::getCreateTime);

        Page<TradeOrder> page = tradeOrderMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        // 批量查商品
        Set<Long> goodsIds = page.getRecords().stream()
                .map(TradeOrder::getGoodsId)
                .collect(Collectors.toSet());
        final Map<Long, Goods> goodsMap;
        if (goodsIds.isEmpty()) {
            goodsMap = Collections.emptyMap();
        } else {
            goodsMap = goodsMapper.selectBatchIds(goodsIds).stream()
                    .collect(Collectors.toMap(Goods::getId, g -> g));
        }

        // 批量查用户（买家 + 卖家）
        Set<Long> userIds = new HashSet<>();
        page.getRecords().forEach(o -> {
            if (o.getBuyerId() != null) userIds.add(o.getBuyerId());
            if (o.getSellerId() != null) userIds.add(o.getSellerId());
        });
        final Map<Long, User> userMap;
        if (userIds.isEmpty()) {
            userMap = Collections.emptyMap();
        } else {
            userMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        }

        Page<AdminOrderVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<AdminOrderVO> voList = page.getRecords().stream().map(o -> {
            AdminOrderVO vo = new AdminOrderVO();
            vo.setId(o.getId());
            vo.setGoodsId(o.getGoodsId());
            vo.setBuyerId(o.getBuyerId());
            vo.setSellerId(o.getSellerId());
            vo.setMessage(o.getMessage());
            vo.setStatus(o.getStatus());
            vo.setCreateTime(o.getCreateTime());
            vo.setUpdateTime(o.getUpdateTime());

            Goods g = goodsMap.get(o.getGoodsId());
            if (g != null) {
                vo.setGoodsTitle(g.getTitle());
                vo.setGoodsPrice(g.getPrice());
            }

            User buyer = userMap.get(o.getBuyerId());
            if (buyer != null) {
                vo.setBuyerUsername(buyer.getUsername());
                vo.setBuyerNickname(buyer.getNickname());
            }

            User seller = userMap.get(o.getSellerId());
            if (seller != null) {
                vo.setSellerUsername(seller.getUsername());
                vo.setSellerNickname(seller.getNickname());
            }

            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }
}