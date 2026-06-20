package com.example.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.secondhand.dto.AdminOrderQueryDTO;
import com.example.secondhand.vo.AdminOrderVO;

public interface AdminOrderService {

    /**
     * 管理员订单列表
     */
    Page<AdminOrderVO> listOrders(AdminOrderQueryDTO query);
}