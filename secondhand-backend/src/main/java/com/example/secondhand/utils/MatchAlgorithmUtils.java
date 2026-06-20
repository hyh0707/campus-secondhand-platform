package com.example.secondhand.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 多特征融合匹配算法工具类
 * <p>
 * 总分 100 分，评分维度：
 * - 分类匹配：20分
 * - 关键词匹配：30分
 * - 价格匹配：20分
 * - 成色匹配：10分
 * - 地点匹配：10分
 * - 热度匹配：10分
 */
public class MatchAlgorithmUtils {

    private static final int SCORE_CATEGORY = 20;
    private static final int SCORE_KEYWORD = 30;
    private static final int SCORE_PRICE = 20;
    private static final int SCORE_CONDITION = 10;
    private static final int SCORE_LOCATION = 10;
    private static final int SCORE_POPULARITY = 10;

    /** 成色等级映射（数值越大越新） */
    private static final Map<String, Integer> CONDITION_RANK = Map.of(
            "brand_new", 5,
            "like_new", 4,
            "good", 3,
            "moderate", 2,
            "worn", 1
    );

    private MatchAlgorithmUtils() {}

    /**
     * 匹配结果
     */
    public static class MatchResult {
        public int score;
        public final List<String> reasons = new ArrayList<>();

        public void addReason(String reason) {
            reasons.add(reason);
        }
    }

    /**
     * 计算商品与求购的匹配分数
     */
    public static MatchResult calculateGoodsDemandMatch(
            Long goodsCategoryId, Long demandCategoryId,
            Set<String> goodsKeywords, Set<String> demandKeywords,
            BigDecimal goodsPrice, BigDecimal demandMinPrice, BigDecimal demandMaxPrice,
            String goodsCondition, String demandExpectedCondition,
            String goodsLocation, String demandExpectedLocation,
            int viewCount, int favoriteCount) {

        MatchResult result = new MatchResult();

        // 1. 分类匹配（20分）
        int categoryScore = goodsCategoryId.equals(demandCategoryId) ? SCORE_CATEGORY : 0;
        result.score += categoryScore;
        if (categoryScore > 0) {
            result.addReason("分类匹配");
        }

        // 2. 关键词匹配（30分）
        double jaccard = KeywordUtils.jaccardSimilarity(goodsKeywords, demandKeywords);
        int keywordScore = (int) Math.round(jaccard * SCORE_KEYWORD);
        result.score += keywordScore;
        if (keywordScore > 0) {
            List<String> matched = KeywordUtils.getMatchedKeywords(goodsKeywords, demandKeywords);
            result.addReason("关键词匹配(" + Math.round(jaccard * 100) + "%): " + String.join(", ", matched));
        }

        // 3. 价格匹配（20分）
        int priceScore = calculatePriceScore(goodsPrice, demandMinPrice, demandMaxPrice);
        result.score += priceScore;
        if (priceScore >= SCORE_PRICE) {
            result.addReason("价格完全匹配(" + goodsPrice + "元)");
        } else if (priceScore > 0) {
            result.addReason("价格部分匹配(" + goodsPrice + "元, 期望" + demandMinPrice + "-" + demandMaxPrice + "元)");
        }

        // 4. 成色匹配（10分）
        int conditionScore = calculateConditionScore(goodsCondition, demandExpectedCondition);
        result.score += conditionScore;
        if (conditionScore == SCORE_CONDITION) {
            result.addReason("成色完全匹配");
        } else if (conditionScore > 0) {
            result.addReason("成色接近");
        }

        // 5. 地点匹配（10分）
        int locationScore = calculateLocationScore(goodsLocation, demandExpectedLocation);
        result.score += locationScore;
        if (locationScore == SCORE_LOCATION) {
            result.addReason("地点匹配");
        } else if (locationScore > 0) {
            result.addReason("地点部分匹配");
        }

        // 6. 热度匹配（10分）
        int popularityScore = calculatePopularityScore(viewCount, favoriteCount);
        result.score += popularityScore;
        if (popularityScore >= 8) {
            result.addReason("高热度商品");
        } else if (popularityScore > 0) {
            result.addReason("有一定热度");
        }

        return result;
    }

    /**
     * 价格匹配评分
     * 商品价格在预算范围内：满分
     * 超出预算：按比例扣分
     */
    private static int calculatePriceScore(BigDecimal price, BigDecimal minPrice, BigDecimal maxPrice) {
        if (price == null || minPrice == null || maxPrice == null) return 0;
        if (price.compareTo(BigDecimal.ZERO) <= 0) return 0;

        if (price.compareTo(minPrice) >= 0 && price.compareTo(maxPrice) <= 0) {
            return SCORE_PRICE; // 在预算范围内，满分
        }

        // 价格超出范围
        if (price.compareTo(maxPrice) > 0) {
            BigDecimal ratio = maxPrice.divide(price, 2, RoundingMode.HALF_UP);
            return (int) Math.round(ratio.doubleValue() * SCORE_PRICE);
        }

        // 价格低于最低预算（买家可能愿意，给一半分）
        if (price.compareTo(minPrice) < 0) {
            BigDecimal ratio = price.divide(minPrice, 2, RoundingMode.HALF_UP);
            return (int) Math.round(ratio.doubleValue() * SCORE_PRICE * 0.6);
        }

        return 0;
    }

    /**
     * 成色匹配评分
     * 完全相同：满分
     * 差一级：8分
     * 差两级：5分
     * 差三级：2分
     */
    private static int calculateConditionScore(String goodsCondition, String demandCondition) {
        if (goodsCondition == null || demandCondition == null) return 0;
        Integer cr1 = CONDITION_RANK.get(goodsCondition);
        Integer cr2 = CONDITION_RANK.get(demandCondition);
        if (cr1 == null || cr2 == null) return 0;

        int diff = Math.abs(cr1 - cr2);
        if (diff == 0) return SCORE_CONDITION;
        if (diff == 1) return 8;
        if (diff == 2) return 5;
        if (diff == 3) return 2;
        return 0;
    }

    /**
     * 地点匹配评分
     * 完全相同：满分
     * 包含关系：6分
     */
    private static int calculateLocationScore(String goodsLocation, String demandLocation) {
        if (goodsLocation == null || demandLocation == null) return 0;
        String gl = goodsLocation.trim();
        String dl = demandLocation.trim();
        if (gl.equals(dl)) return SCORE_LOCATION;
        if (gl.contains(dl) || dl.contains(gl)) return 6;
        return 0;
    }

    /**
     * 热度匹配评分（基于浏览量和收藏量）
     */
    private static int calculatePopularityScore(int viewCount, int favoriteCount) {
        int total = viewCount + favoriteCount * 2;
        if (total >= 50) return SCORE_POPULARITY;
        if (total >= 20) return 8;
        if (total >= 10) return 5;
        if (total >= 5) return 3;
        if (total > 0) return 1;
        return 0;
    }

    /**
     * 计算相似商品评分（用于相似商品推荐）
     */
    public static MatchResult calculateSimilarityScore(
            Long goodsCategoryId, Long targetCategoryId,
            Set<String> goodsKeywords, Set<String> targetKeywords,
            BigDecimal goodsPrice, BigDecimal targetPrice,
            String goodsCondition, String targetCondition) {

        MatchResult result = new MatchResult();

        // 分类匹配（30分）
        int categoryScore = goodsCategoryId.equals(targetCategoryId) ? 30 : 0;
        result.score += categoryScore;
        if (categoryScore > 0) {
            result.addReason("同分类");
        }

        // 关键词匹配（40分）
        double jaccard = KeywordUtils.jaccardSimilarity(goodsKeywords, targetKeywords);
        int keywordScore = (int) Math.round(jaccard * 40);
        result.score += keywordScore;
        if (keywordScore > 0) {
            result.addReason("关键词相似(" + Math.round(jaccard * 100) + "%)");
        }

        // 价格相似（15分）
        if (goodsPrice != null && targetPrice != null && goodsPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal max = goodsPrice.max(targetPrice);
            BigDecimal min = goodsPrice.min(targetPrice);
            BigDecimal ratio = min.divide(max, 2, RoundingMode.HALF_UP);
            int priceScore = (int) Math.round(ratio.doubleValue() * 15);
            result.score += priceScore;
            if (priceScore >= 12) {
                result.addReason("价格接近");
            }
        }

        // 成色相似（15分）
        if (goodsCondition != null && targetCondition != null) {
            int cr1 = CONDITION_RANK.getOrDefault(goodsCondition, 0);
            int cr2 = CONDITION_RANK.getOrDefault(targetCondition, 0);
            int diff = Math.abs(cr1 - cr2);
            if (diff == 0) {
                result.score += 15;
                result.addReason("成色相同");
            } else if (diff == 1) {
                result.score += 10;
                result.addReason("成色接近");
            } else if (diff == 2) {
                result.score += 5;
            }
        }

        return result;
    }
}