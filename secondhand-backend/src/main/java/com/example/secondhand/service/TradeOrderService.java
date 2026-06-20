package com.example.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.secondhand.dto.TradeOrderCreateDTO;
import com.example.secondhand.entity.TradeOrder;
import com.example.secondhand.vo.TradeOrderVO;

public interface TradeOrderService extends IService<TradeOrder> {

    /**
     * 创建交易意向
     */
    void createOrder(Long buyerId, TradeOrderCreateDTO dto);

    /**
     * 更新交易状态
     */
    void updateStatus(Long orderId, Long userId, String status);

    /**
     * 我的购买（我是买家）
     */
    Page<TradeOrderVO> myBuy(Long userId, Integer pageNum, Integer pageSize, String status);

    /**
     * 我的出售（我是卖家）
     */
    Page<TradeOrderVO> mySell(Long userId, Integer pageNum, Integer pageSize, String status);
}