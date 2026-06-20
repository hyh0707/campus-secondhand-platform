package com.example.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("demand")
public class Demand {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long categoryId;

    private String title;

    private String description;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    /**
     * 期望新旧程度：brand_new-全新，like_new-几乎全新，good-良好，moderate-一般，worn-老旧
     */
    private String expectedCondition;

    private String expectedLocation;

    /** 关键词，逗号分隔，用于匹配计算 */
    private String keywords;

    /** 状态：pending-待审核，approved-审核通过，rejected-审核不通过，closed-已关闭 */
    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}