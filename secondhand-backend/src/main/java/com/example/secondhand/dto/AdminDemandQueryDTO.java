package com.example.secondhand.dto;

import lombok.Data;

@Data
public class AdminDemandQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /** 求购状态筛选：pending/approved/rejected/closed */
    private String status;

    /** 关键词（搜索 title、description、keywords） */
    private String keyword;

    /** 分类ID */
    private Long categoryId;
}