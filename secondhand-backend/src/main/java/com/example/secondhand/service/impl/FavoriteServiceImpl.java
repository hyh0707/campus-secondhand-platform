package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.secondhand.common.BusinessException;
import com.example.secondhand.common.ResultCode;
import com.example.secondhand.entity.Favorite;
import com.example.secondhand.entity.Goods;
import com.example.secondhand.entity.GoodsImage;
import com.example.secondhand.mapper.FavoriteMapper;
import com.example.secondhand.mapper.GoodsImageMapper;
import com.example.secondhand.mapper.GoodsMapper;
import com.example.secondhand.service.FavoriteService;
import com.example.secondhand.vo.FavoriteGoodsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final GoodsMapper goodsMapper;
    private final GoodsImageMapper goodsImageMapper;

    @Override
    @Transactional
    public void addFavorite(Long userId, Long goodsId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (goods.getUserId().equals(userId)) {
            throw new BusinessException("不能收藏自己的商品");
        }

        // 检查是否已收藏
        Long count = lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getGoodsId, goodsId)
                .count();
        if (count > 0) {
            throw new BusinessException("已收藏该商品");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setGoodsId(goodsId);
        save(favorite);

        // favorite_count +1
        goods.setFavoriteCount(goods.getFavoriteCount() + 1);
        goodsMapper.updateById(goods);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long goodsId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getGoodsId, goodsId);
        Favorite favorite = getOne(wrapper);
        if (favorite == null) {
            throw new BusinessException("未收藏该商品");
        }

        remove(wrapper);

        // favorite_count -1（不允许小于0）
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods != null && goods.getFavoriteCount() > 0) {
            goods.setFavoriteCount(goods.getFavoriteCount() - 1);
            goodsMapper.updateById(goods);
        }
    }

    @Override
    public Page<FavoriteGoodsVO> listFavorites(Long userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime);

        Page<Favorite> page = page(new Page<>(pageNum, pageSize), wrapper);

        // 批量查询商品
        List<Long> goodsIds = page.getRecords().stream()
                .map(Favorite::getGoodsId)
                .collect(Collectors.toList());
        final Map<Long, Goods> goodsMap;
        if (goodsIds.isEmpty()) {
            goodsMap = Collections.emptyMap();
        } else {
            goodsMap = goodsMapper.selectBatchIds(goodsIds).stream()
                    .collect(Collectors.toMap(Goods::getId, g -> g));
        }

        // 批量查询主图
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

        Page<FavoriteGoodsVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<FavoriteGoodsVO> voList = page.getRecords().stream().map(f -> {
            FavoriteGoodsVO vo = new FavoriteGoodsVO();
            vo.setId(f.getId());
            vo.setFavoriteId(f.getId());
            vo.setGoodsId(f.getGoodsId());
            vo.setCreateTime(f.getCreateTime());

            Goods g = goodsMap.get(f.getGoodsId());
            if (g != null) {
                vo.setTitle(g.getTitle());
                vo.setPrice(g.getPrice());
                vo.setConditionLevel(g.getConditionLevel());
                vo.setStatus(g.getStatus());
                vo.setMainImage(mainImageMap.get(g.getId()));
            }
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }
}