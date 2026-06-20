package com.example.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("recommend_record")
public class RecommendRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long demandId;

    private Long goodsId;

    /** 匹配分数（0.00-100.00） */
    private BigDecimal matchScore;

    /** 匹配等级：high-高匹配(>=70)，medium-中匹配(40-69)，low-低匹配(<40) */
    private String matchLevel;

    /** 匹配原因说明（JSON格式） */
    private String matchReason;

    private LocalDateTime createTime;
}