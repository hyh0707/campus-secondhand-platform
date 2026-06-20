package com.example.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 根据求购匹配到的商品 VO
 */
@Data
public class GoodsMatchVO {

    private Long goodsId;

    private String title;

    private BigDecimal price;

    private String conditionLevel;

    /** 主图 URL */
    private String mainImage;

    private String sellerNickname;

    /** 匹配分数 */
    private Integer matchScore;

    /** 匹配原因列表 */
    private List<String> matchReasons;
}