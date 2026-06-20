package com.example.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.secondhand.entity.TradeOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TradeOrderMapper extends BaseMapper<TradeOrder> {
}