package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.secondhand.common.BusinessException;
import com.example.secondhand.common.ResultCode;
import com.example.secondhand.dto.AdminAuditDTO;
import com.example.secondhand.dto.AdminGoodsQueryDTO;
import com.example.secondhand.entity.Goods;
import com.example.secondhand.entity.GoodsImage;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.GoodsImageMapper;
import com.example.secondhand.mapper.GoodsMapper;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.service.AdminGoodsService;
import com.example.secondhand.vo.AdminGoodsVO;
import com.example.secondhand.vo.SellerVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminGoodsServiceImpl implements AdminGoodsService {

    private final GoodsMapper goodsMapper;
    private final GoodsImageMapper goodsImageMapper;
    private final UserMapper userMapper;

    @Override
    public Page<AdminGoodsVO> listGoods(AdminGoodsQueryDTO query) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();

        // 状态筛选
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Goods::getStatus, query.getStatus());
        }

        // 关键词搜索
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w
                    .like(Goods::getTitle, query.getKeyword())
                    .or()
                    .like(Goods::getDescription, query.getKeyword()));
        }

        // 分类筛选
        if (query.getCategoryId() != null) {
            wrapper.eq(Goods::getCategoryId, query.getCategoryId());
        }

        wrapper.orderByDesc(Goods::getCreateTime);

        Page<Goods> page = goodsMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        // 批量查用户
        Set<Long> userIds = page.getRecords().stream()
                .map(Goods::getUserId)
                .collect(Collectors.toSet());
        final Map<Long, User> userMap;
        if (userIds.isEmpty()) {
            userMap = Collections.emptyMap();
        } else {
            userMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        }

        // 批量查主图
        List<Long> goodsIds = page.getRecords().stream()
                .map(Goods::getId)
                .collect(Collectors.toList());
        final Map<Long, String> mainImageMap;
        if (goodsIds.isEmpty()) {
            mainImageMap = Collections.emptyMap();
        } else {
            mainImageMap = goodsImageMapper.selectList(
                            new LambdaQueryWrapper<GoodsImage>()
                                    .in(GoodsImage::getGoodsId, goodsIds)
                                    .orderByAsc(GoodsImage::getSort))
                    .stream()
                    .collect(Collectors.toMap(
                            GoodsImage::getGoodsId,
                            GoodsImage::getImageUrl,
                            (e, r) -> e));
        }

        Page<AdminGoodsVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<AdminGoodsVO> voList = page.getRecords().stream().map(g -> {
            AdminGoodsVO vo = new AdminGoodsVO();
            vo.setId(g.getId());
            vo.setUserId(g.getUserId());
            vo.setCategoryId(g.getCategoryId());
            vo.setTitle(g.getTitle());
            vo.setDescription(g.getDescription());
            vo.setPrice(g.getPrice());
            vo.setConditionLevel(g.getConditionLevel());
            vo.setTradeLocation(g.getTradeLocation());
            vo.setContactInfo(g.getContactInfo());
            vo.setNegotiable(g.getNegotiable());
            vo.setViewCount(g.getViewCount());
            vo.setFavoriteCount(g.getFavoriteCount());
            vo.setStatus(g.getStatus());
            vo.setCreateTime(g.getCreateTime());
            vo.setUpdateTime(g.getUpdateTime());
            vo.setMainImage(mainImageMap.get(g.getId()));

            User u = userMap.get(g.getUserId());
            if (u != null) {
                SellerVO seller = new SellerVO();
                seller.setId(u.getId());
                seller.setUsername(u.getUsername());
                seller.setNickname(u.getNickname());
                seller.setAvatar(u.getAvatar());
                vo.setSeller(seller);
            }
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional
    public void auditGoods(Long goodsId, AdminAuditDTO dto) {
        if (!"approved".equals(dto.getStatus()) && !"rejected".equals(dto.getStatus())) {
            throw new BusinessException("审核状态只允许 approved 或 rejected");
        }

        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!"pending".equals(goods.getStatus())) {
            throw new BusinessException("只能审核待审核状态的商品");
        }

        goods.setStatus(dto.getStatus());
        goodsMapper.updateById(goods);
    }

    @Override
    @Transactional
    public void offlineGoods(Long goodsId, String reason) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if ("sold".equals(goods.getStatus())) {
            throw new BusinessException("已交易的订单不能下架");
        }

        goods.setStatus("offline");
        goodsMapper.updateById(goods);
    }
}