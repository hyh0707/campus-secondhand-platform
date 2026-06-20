package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.secondhand.entity.Demand;
import com.example.secondhand.entity.Goods;
import com.example.secondhand.entity.TradeOrder;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.*;
import com.example.secondhand.service.AdminStatisticsService;
import com.example.secondhand.vo.AdminStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    private final UserMapper userMapper;
    private final GoodsMapper goodsMapper;
    private final DemandMapper demandMapper;
    private final TradeOrderMapper tradeOrderMapper;
    private final FavoriteMapper favoriteMapper;

    @Override
    public AdminStatisticsVO overview() {
        AdminStatisticsVO vo = new AdminStatisticsVO();

        // 用户统计
        vo.setUserCount(userMapper.selectCount(null));
        vo.setDisabledUserCount(userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getStatus, 0)));

        // 今日用户
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        vo.setTodayUserCount(userMapper.selectCount(
                new LambdaQueryWrapper<User>().between(User::getCreateTime, todayStart, todayEnd)));

        // 商品统计
        vo.setGoodsCount(goodsMapper.selectCount(null));
        vo.setPendingGoodsCount(goodsMapper.selectCount(
                new LambdaQueryWrapper<Goods>().eq(Goods::getStatus, "pending")));
        vo.setApprovedGoodsCount(goodsMapper.selectCount(
                new LambdaQueryWrapper<Goods>().eq(Goods::getStatus, "approved")));
        vo.setSoldGoodsCount(goodsMapper.selectCount(
                new LambdaQueryWrapper<Goods>().eq(Goods::getStatus, "sold")));
        vo.setTodayGoodsCount(goodsMapper.selectCount(
                new LambdaQueryWrapper<Goods>().between(Goods::getCreateTime, todayStart, todayEnd)));

        // 求购统计
        vo.setDemandCount(demandMapper.selectCount(null));
        vo.setPendingDemandCount(demandMapper.selectCount(
                new LambdaQueryWrapper<Demand>().eq(Demand::getStatus, "pending")));
        vo.setApprovedDemandCount(demandMapper.selectCount(
                new LambdaQueryWrapper<Demand>().eq(Demand::getStatus, "approved")));
        vo.setTodayDemandCount(demandMapper.selectCount(
                new LambdaQueryWrapper<Demand>().between(Demand::getCreateTime, todayStart, todayEnd)));

        // 交易意向统计
        vo.setOrderCount(tradeOrderMapper.selectCount(null));
        vo.setPendingOrderCount(tradeOrderMapper.selectCount(
                new LambdaQueryWrapper<TradeOrder>().eq(TradeOrder::getStatus, "pending")));
        vo.setCompletedOrderCount(tradeOrderMapper.selectCount(
                new LambdaQueryWrapper<TradeOrder>().eq(TradeOrder::getStatus, "completed")));
        vo.setTodayOrderCount(tradeOrderMapper.selectCount(
                new LambdaQueryWrapper<TradeOrder>().between(TradeOrder::getCreateTime, todayStart, todayEnd)));

        // 收藏统计
        vo.setFavoriteCount(favoriteMapper.selectCount(null));

        return vo;
    }
}