package com.example.secondhand.vo;

import lombok.Data;

@Data
public class AdminStatisticsVO {

    /** 用户总数 */
    private Long userCount;

    /** 禁用用户数 */
    private Long disabledUserCount;

    /** 商品总数 */
    private Long goodsCount;

    /** 待审核商品数 */
    private Long pendingGoodsCount;

    /** 已上架商品数 */
    private Long approvedGoodsCount;

    /** 已售出商品数 */
    private Long soldGoodsCount;

    /** 求购总数 */
    private Long demandCount;

    /** 待审核求购数 */
    private Long pendingDemandCount;

    /** 已通过求购数 */
    private Long approvedDemandCount;

    /** 交易意向总数 */
    private Long orderCount;

    /** 待确认交易意向数 */
    private Long pendingOrderCount;

    /** 已完成交易数 */
    private Long completedOrderCount;

    /** 收藏总数 */
    private Long favoriteCount;

    /** 今日新增用户数 */
    private Long todayUserCount;

    /** 今日新增商品数 */
    private Long todayGoodsCount;

    /** 今日新增求购数 */
    private Long todayDemandCount;

    /** 今日新增交易意向数 */
    private Long todayOrderCount;
}