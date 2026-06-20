package com.example.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GoodsListVO {

    private Long id;

    private String title;

    private BigDecimal price;

    private String conditionLevel;

    private String status;

    private Integer viewCount;

    private Integer favoriteCount;

    private LocalDateTime createTime;

    /** 主图 URL（第一张图片，没有则为 null） */
    private String mainImage;

    /** 卖家信息 */
    private SellerVO seller;
}