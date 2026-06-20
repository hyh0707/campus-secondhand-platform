package com.example.secondhand.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GoodsAddDTO {

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "描述不能为空")
    private String description;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    private String conditionLevel;

    private String tradeLocation;

    private String contactInfo;

    /** 是否可议价：0-否，1-是 */
    private Integer negotiable;

    /** 图片 URL 列表（来自 /api/upload/image） */
    private List<String> imageUrls;
}