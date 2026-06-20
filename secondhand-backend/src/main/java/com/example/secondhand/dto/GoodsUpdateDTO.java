package com.example.secondhand.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GoodsUpdateDTO {

    private Long categoryId;

    private String title;

    private String description;

    private BigDecimal price;

    private String conditionLevel;

    private String tradeLocation;

    private String contactInfo;

    private Integer negotiable;

    /** 图片 URL 列表 */
    private List<String> imageUrls;
}