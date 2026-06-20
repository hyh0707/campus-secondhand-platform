package com.example.secondhand.dto;

import lombok.Data;

@Data
public class AdminUserQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /** 关键词（搜索 username、nickname、phone、email） */
    private String keyword;

    /** 状态筛选：1-正常，0-禁用 */
    private Integer status;
}