package com.example.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GoodsDetailVO {

    private Long id;

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

    /** 商品图片列表 */
    private List<GoodsImageVO> images;

    /** 卖家信息 */
    private SellerVO seller;
}