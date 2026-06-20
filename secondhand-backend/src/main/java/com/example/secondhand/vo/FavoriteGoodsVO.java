package com.example.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FavoriteGoodsVO {

    private Long id;

    /** 关联的收藏记录ID（favorite 表 ID） */
    private Long favoriteId;

    private Long goodsId;

    private String title;

    private BigDecimal price;

    private String conditionLevel;

    private String status;

    private String mainImage;

    private LocalDateTime createTime;
}