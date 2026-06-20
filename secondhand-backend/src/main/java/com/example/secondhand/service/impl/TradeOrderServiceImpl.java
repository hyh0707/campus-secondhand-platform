package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.secondhand.common.BusinessException;
import com.example.secondhand.common.ResultCode;
import com.example.secondhand.dto.TradeOrderCreateDTO;
import com.example.secondhand.entity.Goods;
import com.example.secondhand.entity.TradeOrder;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.GoodsMapper;
import com.example.secondhand.mapper.TradeOrderMapper;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.service.TradeOrderService;
import com.example.secondhand.vo.TradeOrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeOrderServiceImpl extends ServiceImpl<TradeOrderMapper, TradeOrder> implements TradeOrderService {

    private final GoodsMapper goodsMapper;
    private final UserMapper userMapper;

    private static final Set<String> PENDING_OR_ACCEPTED = Set.of("pending", "accepted");

    @Override
    @Transactional
    public void createOrder(Long buyerId, TradeOrderCreateDTO dto) {
        Goods goods = goodsMapper.selectById(dto.getGoodsId());
        if (goods == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!"approved".equals(goods.getStatus())) {
            throw new BusinessException("该商品当前不可交易");
        }
        if (goods.getUserId().equals(buyerId)) {
            throw new BusinessException("不能购买自己的商品");
        }

        // 同一买家对同一商品不能有未完成的交易
        Long count = lambdaQuery()
                .eq(TradeOrder::getBuyerId, buyerId)
                .eq(TradeOrder::getGoodsId, dto.getGoodsId())
                .in(TradeOrder::getStatus, "pending", "accepted")
                .count();
        if (count > 0) {
            throw new BusinessException("您已有未完成的交易意向");
        }

        TradeOrder order = new TradeOrder();
        order.setBuyerId(buyerId);
        order.setSellerId(goods.getUserId());
        order.setGoodsId(dto.getGoodsId());
        order.setMessage(dto.getMessage());
        order.setStatus("pending");
        save(order);
    }

    @Override
    @Transactional
    public void updateStatus(Long orderId, Long userId, String status) {
        TradeOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        boolean isBuyer = order.getBuyerId().equals(userId);
        boolean isSeller = order.getSellerId().equals(userId);

        if (!isBuyer && !isSeller) {
            throw new BusinessException("无权操作该交易");
        }

        String oldStatus = order.getStatus();

        if ("accepted".equals(status)) {
            // 卖家可以将 pending 改为 accepted
            if (!isSeller || !"pending".equals(oldStatus)) {
                throw new BusinessException("只能接受待确认的交易");
            }
        } else if ("rejected".equals(status)) {
            // 卖家可以将 pending 改为 rejected
            if (!isSeller || !"pending".equals(oldStatus)) {
                throw new BusinessException("只能拒绝待确认的交易");
            }
        } else if ("completed".equals(status)) {
            // 买家或卖家可以将 accepted 改为 completed
            if (!"accepted".equals(oldStatus)) {
                throw new BusinessException("只能完成已接受的交易");
            }
            // 商品状态改为 sold
            Goods goods = goodsMapper.selectById(order.getGoodsId());
            if (goods != null) {
                goods.setStatus("sold");
                goodsMapper.updateById(goods);
            }
        } else if ("cancelled".equals(status)) {
            // 买家可以将 pending 或 accepted 改为 cancelled
            if (!isBuyer || !PENDING_OR_ACCEPTED.contains(oldStatus)) {
                throw new BusinessException("只能取消待确认或已接受的交易");
            }
        } else {
            throw new BusinessException("无效的状态值");
        }

        order.setStatus(status);
        updateById(order);
    }

    @Override
    public Page<TradeOrderVO> myBuy(Long userId, Integer pageNum, Integer pageSize, String status) {
        LambdaQueryWrapper<TradeOrder> wrapper = new LambdaQueryWrapper<TradeOrder>()
                .eq(TradeOrder::getBuyerId, userId);

        if (status != null && !status.isEmpty()) {
            wrapper.eq(TradeOrder::getStatus, status);
        }

        wrapper.orderByDesc(TradeOrder::getCreateTime);
        Page<TradeOrder> page = page(new Page<>(pageNum, pageSize), wrapper);

        return buildTradeOrderVOPage(page);
    }

    @Override
    public Page<TradeOrderVO> mySell(Long userId, Integer pageNum, Integer pageSize, String status) {
        LambdaQueryWrapper<TradeOrder> wrapper = new LambdaQueryWrapper<TradeOrder>()
                .eq(TradeOrder::getSellerId, userId);

        if (status != null && !status.isEmpty()) {
            wrapper.eq(TradeOrder::getStatus, status);
        }

        wrapper.orderByDesc(TradeOrder::getCreateTime);
        Page<TradeOrder> page = page(new Page<>(pageNum, pageSize), wrapper);

        return buildTradeOrderVOPage(page);
    }

    // ==================== 私有方法 ====================

    private Page<TradeOrderVO> buildTradeOrderVOPage(Page<TradeOrder> page) {
        // 批量查用户
        Set<Long> userIds = new HashSet<>();
        page.getRecords().forEach(o -> {
            userIds.add(o.getBuyerId());
            userIds.add(o.getSellerId());
        });
        final Map<Long, User> userMap;
        if (userIds.isEmpty()) {
            userMap = Collections.emptyMap();
        } else {
            userMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        }

        // 批量查商品
        List<Long> goodsIds = page.getRecords().stream()
                .map(TradeOrder::getGoodsId)
                .distinct()
                .collect(Collectors.toList());
        final Map<Long, Goods> goodsMap;
        if (goodsIds.isEmpty()) {
            goodsMap = Collections.emptyMap();
        } else {
            goodsMap = goodsMapper.selectBatchIds(goodsIds).stream()
                    .collect(Collectors.toMap(Goods::getId, g -> g));
        }

        Page<TradeOrderVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<TradeOrderVO> voList = page.getRecords().stream().map(o -> {
            TradeOrderVO vo = new TradeOrderVO();
            vo.setId(o.getId());
            vo.setBuyerId(o.getBuyerId());
            vo.setSellerId(o.getSellerId());
            vo.setGoodsId(o.getGoodsId());
            vo.setMessage(o.getMessage());
            vo.setStatus(o.getStatus());
            vo.setCreateTime(o.getCreateTime());
            vo.setUpdateTime(o.getUpdateTime());

            User buyer = userMap.get(o.getBuyerId());
            if (buyer != null) {
                vo.setBuyerName(buyer.getNickname() != null ? buyer.getNickname() : buyer.getUsername());
            }
            User seller = userMap.get(o.getSellerId());
            if (seller != null) {
                vo.setSellerName(seller.getNickname() != null ? seller.getNickname() : seller.getUsername());
            }

            Goods g = goodsMap.get(o.getGoodsId());
            if (g != null) {
                vo.setGoodsTitle(g.getTitle());
                vo.setGoodsPrice(g.getPrice());
            }
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }
}