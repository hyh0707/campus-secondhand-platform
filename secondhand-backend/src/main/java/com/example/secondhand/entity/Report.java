package com.example.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("report")
public class Report {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 举报目标类型：goods-商品，user-用户 */
    private String targetType;

    private Long targetId;

    private String reason;

    /** 处理状态：pending-待处理，resolved-已处理，dismissed-已驳回 */
    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}