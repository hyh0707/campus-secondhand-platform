package com.example.secondhand.dto;

import lombok.Data;

@Data
public class AdminGoodsQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /** 商品状态筛选：pending/approved/rejected/offline/sold */
    private String status;

    /** 关键词（搜索 title、description） */
    private String keyword;

    /** 分类ID */
    private Long categoryId;
}