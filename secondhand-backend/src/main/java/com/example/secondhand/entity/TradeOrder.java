package com.example.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("trade_order")
public class TradeOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 买家用户ID（发起交易意向方） */
    private Long buyerId;

    /** 卖家用户ID（商品发布方） */
    private Long sellerId;

    private Long goodsId;

    private String message;

    /** 状态：pending-待确认，accepted-已接受，rejected-已拒绝，completed-已完成，cancelled-已取消 */
    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}