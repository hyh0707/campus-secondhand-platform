package com.example.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 推荐商品 VO
 */
@Data
public class RecommendGoodsVO {

    private Long goodsId;

    private String title;

    private BigDecimal price;

    private String conditionLevel;

    /** 主图 URL */
    private String mainImage;

    private String sellerNickname;

    /** 推荐分数 */
    private Integer recommendScore;

    /** 推荐原因列表 */
    private List<String> recommendReasons;
}