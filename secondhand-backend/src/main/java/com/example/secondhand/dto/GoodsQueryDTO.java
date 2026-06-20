package com.example.secondhand.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /** 关键词（搜索 title、description） */
    private String keyword;

    /** 分类ID */
    private Long categoryId;

    /** 最低价 */
    private BigDecimal minPrice;

    /** 最高价 */
    private BigDecimal maxPrice;

    /** 新旧程度 */
    private String conditionLevel;

    /** 状态筛选 */
    private String status;
}