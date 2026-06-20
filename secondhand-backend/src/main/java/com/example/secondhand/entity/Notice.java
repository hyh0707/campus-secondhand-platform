package com.example.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notice")
public class Notice {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    /** 状态：0-下架，1-发布 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}