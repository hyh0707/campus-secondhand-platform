package com.example.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminOrderVO {

    private Long id;

    private Long goodsId;

    private String goodsTitle;

    private BigDecimal goodsPrice;

    private Long buyerId;

    private String buyerUsername;

    private String buyerNickname;

    private Long sellerId;

    private String sellerUsername;

    private String sellerNickname;

    private String message;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}