package com.example.secondhand.utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 关键词提取与匹配工具类
 */
public class KeywordUtils {

    private static final String DELIMITERS = "[，,、。；;\\s]+";

    private KeywordUtils() {}

    /**
     * 从文本中提取关键词集合
     * 来源：逗号分隔关键词 + 标题/描述中的 2-char 片段
     */
    public static Set<String> extractKeywords(String title, String description, String keywords) {
        Set<String> result = new HashSet<>();

        // 1. 解析逗号分隔的关键词
        if (keywords != null && !keywords.isBlank()) {
            for (String kw : keywords.split(DELIMITERS)) {
                String trimmed = kw.trim();
                if (!trimmed.isEmpty()) {
                    result.add(trimmed.toLowerCase());
                }
            }
        }

        // 2. 从标题提取 2-char 片段
        if (title != null) {
            extractBigrams(title, result);
        }

        // 3. 从描述提取 2-char 片段
        if (description != null) {
            extractBigrams(description, result);
        }

        return result;
    }

    /**
     * 提取文本中的 2-char 片段（用于中文分词降级方案）
     */
    private static void extractBigrams(String text, Set<String> result) {
        String cleaned = text.replaceAll("[\\s\\p{Punct}，。；：、！？（）【】《》\"'\"“”‘’…—]", "");
        for (int i = 0; i < cleaned.length() - 1; i++) {
            result.add(cleaned.substring(i, i + 2).toLowerCase());
        }
    }

    /**
     * 计算 Jaccard 相似度
     * @return 0.0 ~ 1.0
     */
    public static double jaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() && set2.isEmpty()) return 0;
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }

    /**
     * 获取匹配到的关键词列表（用于可解释性）
     */
    public static List<String> getMatchedKeywords(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection.stream().sorted().collect(Collectors.toList());
    }
}