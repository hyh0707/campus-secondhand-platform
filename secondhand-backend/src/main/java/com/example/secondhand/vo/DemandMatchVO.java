package com.example.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 根据商品匹配到的求购 VO
 */
@Data
public class DemandMatchVO {

    private Long demandId;

    private String title;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private String expectedCondition;

    private String expectedLocation;

    private String userNickname;

    /** 匹配分数 */
    private Integer matchScore;

    /** 匹配原因列表 */
    private List<String> matchReasons;
}