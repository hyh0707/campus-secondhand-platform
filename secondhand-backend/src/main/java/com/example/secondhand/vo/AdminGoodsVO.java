package com.example.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminGoodsVO {

    private Long id;

    private Long userId;

    private Long categoryId;

    private String title;

    private String description;

    private BigDecimal price;

    private String conditionLevel;

    private String tradeLocation;

    private String contactInfo;

    private Integer negotiable;

    private Integer viewCount;

    private Integer favoriteCount;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 主图 */
    private String mainImage;

    /** 发布者信息 */
    private SellerVO seller;
}