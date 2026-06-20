package com.example.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DemandListVO {

    private Long id;

    private Long categoryId;

    private String title;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private String expectedCondition;

    private String expectedLocation;

    private String keywords;

    private String status;

    private LocalDateTime createTime;

    /** 发布者信息 */
    private DemandUserVO user;
}