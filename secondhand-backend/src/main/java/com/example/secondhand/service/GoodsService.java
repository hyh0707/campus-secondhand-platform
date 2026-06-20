package com.example.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.secondhand.dto.GoodsAddDTO;
import com.example.secondhand.dto.GoodsQueryDTO;
import com.example.secondhand.dto.GoodsUpdateDTO;
import com.example.secondhand.entity.Goods;
import com.example.secondhand.vo.GoodsDetailVO;
import com.example.secondhand.vo.GoodsListVO;

public interface GoodsService extends IService<Goods> {

    /**
     * 商品列表（分页 + 筛选）
     */
    Page<GoodsListVO> listGoods(GoodsQueryDTO query);

    /**
     * 商品详情
     */
    GoodsDetailVO getDetail(Long id);

    /**
     * 发布商品
     */
    void addGoods(Long userId, GoodsAddDTO dto);

    /**
     * 修改商品
     */
    void updateGoods(Long goodsId, Long userId, GoodsUpdateDTO dto);

    /**
     * 删除商品（软删除 → offline）
     */
    void deleteGoods(Long goodsId, Long userId);

    /**
     * 我的商品列表
     */
    Page<GoodsListVO> myGoods(Long userId, Integer pageNum, Integer pageSize, String status);
}