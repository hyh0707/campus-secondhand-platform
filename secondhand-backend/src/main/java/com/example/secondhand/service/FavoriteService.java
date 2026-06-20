package com.example.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.secondhand.entity.Favorite;
import com.example.secondhand.vo.FavoriteGoodsVO;

public interface FavoriteService extends IService<Favorite> {

    /**
     * 添加收藏
     */
    void addFavorite(Long userId, Long goodsId);

    /**
     * 取消收藏
     */
    void removeFavorite(Long userId, Long goodsId);

    /**
     * 收藏列表
     */
    Page<FavoriteGoodsVO> listFavorites(Long userId, Integer pageNum, Integer pageSize);
}