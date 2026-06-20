package com.example.secondhand.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DemandAddDTO {

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "描述不能为空")
    private String description;

    @NotNull(message = "最低预算不能为空")
    private BigDecimal minPrice;

    @NotNull(message = "最高预算不能为空")
    private BigDecimal maxPrice;

    /** 期望新旧程度 */
    private String expectedCondition;

    /** 期望交易地点 */
    private String expectedLocation;

    /** 关键词，逗号分隔，为空时从 title 和 description 自动提取 */
    private String keywords;
}