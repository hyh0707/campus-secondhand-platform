package com.example.secondhand.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DemandQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /** 关键词（搜索 title、description、keywords） */
    private String keyword;

    /** 分类ID */
    private Long categoryId;

    /** 最低预算 */
    private BigDecimal minPrice;

    /** 最高预算 */
    private BigDecimal maxPrice;

    /** 状态筛选 */
    private String status;
}