package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.secondhand.common.BusinessException;
import com.example.secondhand.common.ResultCode;
import com.example.secondhand.dto.GoodsAddDTO;
import com.example.secondhand.dto.GoodsQueryDTO;
import com.example.secondhand.dto.GoodsUpdateDTO;
import com.example.secondhand.entity.Goods;
import com.example.secondhand.entity.GoodsImage;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.GoodsImageMapper;
import com.example.secondhand.mapper.GoodsMapper;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.service.GoodsService;
import com.example.secondhand.vo.GoodsDetailVO;
import com.example.secondhand.vo.GoodsImageVO;
import com.example.secondhand.vo.GoodsListVO;
import com.example.secondhand.vo.SellerVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    private final GoodsImageMapper goodsImageMapper;
    private final UserMapper userMapper;

    @Override
    public Page<GoodsListVO> listGoods(GoodsQueryDTO query) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();

        // 默认只展示审核通过的商品
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Goods::getStatus, query.getStatus());
        } else {
            wrapper.eq(Goods::getStatus, "approved");
        }

        // 关键词搜索 title / description
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

        // 价格区间
        if (query.getMinPrice() != null) {
            wrapper.ge(Goods::getPrice, query.getMinPrice());
        }
        if (query.getMaxPrice() != null) {
            wrapper.le(Goods::getPrice, query.getMaxPrice());
        }

        // 新旧程度
        if (StringUtils.hasText(query.getConditionLevel())) {
            wrapper.eq(Goods::getConditionLevel, query.getConditionLevel());
        }

        wrapper.orderByDesc(Goods::getCreateTime);

        Page<Goods> page = page(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        // 收集 userId 批量查询 seller 信息
        List<Long> userIds = page.getRecords().stream()
                .map(Goods::getUserId)
                .distinct()
                .collect(Collectors.toList());
        final Map<Long, User> userMap;
        if (userIds.isEmpty()) {
            userMap = Collections.emptyMap();
        } else {
            userMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        }

        // 收集 goodsId 批量查询主图
        List<Long> goodsIds = page.getRecords().stream()
                .map(Goods::getId)
                .collect(Collectors.toList());
        final Map<Long, String> mainImageMap;
        if (!goodsIds.isEmpty()) {
            mainImageMap = goodsImageMapper.selectList(
                            new LambdaQueryWrapper<GoodsImage>()
                                    .in(GoodsImage::getGoodsId, goodsIds)
                                    .orderByAsc(GoodsImage::getSort))
                    .stream()
                    .collect(Collectors.toMap(
                            GoodsImage::getGoodsId,
                            GoodsImage::getImageUrl,
                            (existing, replacement) -> existing));
        } else {
            mainImageMap = Collections.emptyMap();
        }

        // 构建 VO
        Page<GoodsListVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<GoodsListVO> voList = page.getRecords().stream().map(g -> {
            GoodsListVO vo = new GoodsListVO();
            vo.setId(g.getId());
            vo.setTitle(g.getTitle());
            vo.setPrice(g.getPrice());
            vo.setConditionLevel(g.getConditionLevel());
            vo.setStatus(g.getStatus());
            vo.setViewCount(g.getViewCount());
            vo.setFavoriteCount(g.getFavoriteCount());
            vo.setCreateTime(g.getCreateTime());
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
    public GoodsDetailVO getDetail(Long id) {
        Goods goods = getById(id);
        if (goods == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 浏览量 +1
        lambdaUpdate().setSql("view_count = view_count + 1").eq(Goods::getId, id).update();

        GoodsDetailVO vo = new GoodsDetailVO();
        vo.setId(goods.getId());
        vo.setTitle(goods.getTitle());
        vo.setDescription(goods.getDescription());
        vo.setPrice(goods.getPrice());
        vo.setConditionLevel(goods.getConditionLevel());
        vo.setTradeLocation(goods.getTradeLocation());
        vo.setContactInfo(goods.getContactInfo());
        vo.setNegotiable(goods.getNegotiable());
        vo.setViewCount(goods.getViewCount() + 1);
        vo.setFavoriteCount(goods.getFavoriteCount());
        vo.setStatus(goods.getStatus());
        vo.setCreateTime(goods.getCreateTime());
        vo.setUpdateTime(goods.getUpdateTime());

        // 图片列表
        List<GoodsImage> images = goodsImageMapper.selectList(
                new LambdaQueryWrapper<GoodsImage>()
                        .eq(GoodsImage::getGoodsId, id)
                        .orderByAsc(GoodsImage::getSort));
        vo.setImages(images.stream().map(img -> {
            GoodsImageVO imgVO = new GoodsImageVO();
            imgVO.setId(img.getId());
            imgVO.setImageUrl(img.getImageUrl());
            imgVO.setSort(img.getSort());
            return imgVO;
        }).collect(Collectors.toList()));

        // 卖家信息
        User seller = userMapper.selectById(goods.getUserId());
        if (seller != null) {
            SellerVO sellerVO = new SellerVO();
            sellerVO.setId(seller.getId());
            sellerVO.setUsername(seller.getUsername());
            sellerVO.setNickname(seller.getNickname());
            sellerVO.setAvatar(seller.getAvatar());
            vo.setSeller(sellerVO);
        }

        return vo;
    }

    @Override
    @Transactional
    public void addGoods(Long userId, GoodsAddDTO dto) {
        Goods goods = new Goods();
        goods.setUserId(userId);
        goods.setCategoryId(dto.getCategoryId());
        goods.setTitle(dto.getTitle());
        goods.setDescription(dto.getDescription());
        goods.setPrice(dto.getPrice());
        goods.setConditionLevel(dto.getConditionLevel());
        goods.setTradeLocation(dto.getTradeLocation());
        goods.setContactInfo(dto.getContactInfo());
        goods.setNegotiable(dto.getNegotiable() != null ? dto.getNegotiable() : 0);
        goods.setViewCount(0);
        goods.setFavoriteCount(0);
        goods.setStatus("pending");
        save(goods);

        // 保存图片
        saveImages(goods.getId(), dto.getImageUrls());
    }

    @Override
    @Transactional
    public void updateGoods(Long goodsId, Long userId, GoodsUpdateDTO dto) {
        Goods goods = getById(goodsId);
        if (goods == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!goods.getUserId().equals(userId)) {
            throw new BusinessException("只能修改自己发布的商品");
        }
        if ("sold".equals(goods.getStatus())) {
            throw new BusinessException("已交易商品不可修改");
        }

        if (dto.getCategoryId() != null) {
            goods.setCategoryId(dto.getCategoryId());
        }
        if (dto.getTitle() != null) {
            goods.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            goods.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            goods.setPrice(dto.getPrice());
        }
        if (dto.getConditionLevel() != null) {
            goods.setConditionLevel(dto.getConditionLevel());
        }
        if (dto.getTradeLocation() != null) {
            goods.setTradeLocation(dto.getTradeLocation());
        }
        if (dto.getContactInfo() != null) {
            goods.setContactInfo(dto.getContactInfo());
        }
        if (dto.getNegotiable() != null) {
            goods.setNegotiable(dto.getNegotiable());
        }
        // 修改后变更为待审核
        goods.setStatus("pending");
        updateById(goods);

        // 更新图片（先删后加）
        if (dto.getImageUrls() != null) {
            goodsImageMapper.delete(
                    new LambdaQueryWrapper<GoodsImage>().eq(GoodsImage::getGoodsId, goodsId));
            saveImages(goodsId, dto.getImageUrls());
        }
    }

    @Override
    public void deleteGoods(Long goodsId, Long userId) {
        Goods goods = getById(goodsId);
        if (goods == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!goods.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己发布的商品");
        }
        if ("sold".equals(goods.getStatus())) {
            throw new BusinessException("已交易商品不可删除");
        }

        goods.setStatus("offline");
        updateById(goods);
    }

    @Override
    public Page<GoodsListVO> myGoods(Long userId, Integer pageNum, Integer pageSize, String status) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
                .eq(Goods::getUserId, userId)
                .ne(Goods::getStatus, "offline");  // 默认不展示已删除的

        if (StringUtils.hasText(status)) {
            wrapper.eq(Goods::getStatus, status);
        }

        wrapper.orderByDesc(Goods::getCreateTime);

        Page<Goods> page = page(new Page<>(pageNum, pageSize), wrapper);

        // 批量查主图
        List<Long> goodsIds = page.getRecords().stream()
                .map(Goods::getId)
                .collect(Collectors.toList());
        final Map<Long, String> mainImageMap;
        if (!goodsIds.isEmpty()) {
            mainImageMap = goodsImageMapper.selectList(
                            new LambdaQueryWrapper<GoodsImage>()
                                    .in(GoodsImage::getGoodsId, goodsIds)
                                    .orderByAsc(GoodsImage::getSort))
                    .stream()
                    .collect(Collectors.toMap(
                            GoodsImage::getGoodsId,
                            GoodsImage::getImageUrl,
                            (existing, replacement) -> existing));
        } else {
            mainImageMap = Collections.emptyMap();
        }

        Page<GoodsListVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<GoodsListVO> voList = page.getRecords().stream().map(g -> {
            GoodsListVO vo = new GoodsListVO();
            vo.setId(g.getId());
            vo.setTitle(g.getTitle());
            vo.setPrice(g.getPrice());
            vo.setConditionLevel(g.getConditionLevel());
            vo.setStatus(g.getStatus());
            vo.setViewCount(g.getViewCount());
            vo.setFavoriteCount(g.getFavoriteCount());
            vo.setCreateTime(g.getCreateTime());
            vo.setMainImage(mainImageMap.get(g.getId()));
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    // ==================== 私有方法 ====================

    private void saveImages(Long goodsId, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        for (int i = 0; i < imageUrls.size(); i++) {
            GoodsImage img = new GoodsImage();
            img.setGoodsId(goodsId);
            img.setImageUrl(imageUrls.get(i));
            img.setSort(i);
            goodsImageMapper.insert(img);
        }
    }
}