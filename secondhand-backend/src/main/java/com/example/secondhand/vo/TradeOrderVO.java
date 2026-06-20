package com.example.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradeOrderVO {

    private Long id;

    private Long buyerId;

    private String buyerName;

    private Long sellerId;

    private String sellerName;

    private Long goodsId;

    private String goodsTitle;

    private BigDecimal goodsPrice;

    private String message;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}