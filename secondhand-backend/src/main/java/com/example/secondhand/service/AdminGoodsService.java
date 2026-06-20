package com.example.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.secondhand.dto.AdminAuditDTO;
import com.example.secondhand.dto.AdminGoodsQueryDTO;
import com.example.secondhand.vo.AdminGoodsVO;

public interface AdminGoodsService {

    /**
     * 管理员商品列表
     */
    Page<AdminGoodsVO> listGoods(AdminGoodsQueryDTO query);

    /**
     * 审核商品
     */
    void auditGoods(Long goodsId, AdminAuditDTO dto);

    /**
     * 下架商品
     */
    void offlineGoods(Long goodsId, String reason);
}