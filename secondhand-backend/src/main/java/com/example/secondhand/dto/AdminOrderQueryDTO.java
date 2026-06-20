package com.example.secondhand.dto;

import lombok.Data;

@Data
public class AdminOrderQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /** 状态筛选：pending/accepted/rejected/completed/cancelled */
    private String status;

    /** 买家ID */
    private Long buyerId;

    /** 卖家ID */
    private Long sellerId;

    /** 商品ID */
    private Long goodsId;
}