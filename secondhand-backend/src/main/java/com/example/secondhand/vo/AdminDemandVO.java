package com.example.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminDemandVO {

    private Long id;

    private Long userId;

    private Long categoryId;

    private String title;

    private String description;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private String expectedCondition;

    private String expectedLocation;

    private String keywords;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 发布者信息 */
    private SellerVO user;
}