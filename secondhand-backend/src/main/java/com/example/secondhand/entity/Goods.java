package com.example.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("goods")
public class Goods {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long categoryId;

    private String title;

    private String description;

    private BigDecimal price;

    /**
     * 新旧程度：brand_new-全新，like_new-几乎全新，good-良好，moderate-一般，worn-老旧
     */
    private String conditionLevel;

    private String tradeLocation;

    private String contactInfo;

    /** 是否可议价：0-否，1-是 */
    private Integer negotiable;

    /** 浏览次数 */
    private Integer viewCount;

    /** 收藏次数 */
    private Integer favoriteCount;

    /** 状态：pending-待审核，approved-审核通过，rejected-审核不通过，offline-已下架，sold-已交易 */
    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}