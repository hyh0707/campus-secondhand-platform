package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.secondhand.entity.TradeOrder;
import com.example.secondhand.mapper.TradeOrderMapper;
import com.example.secondhand.service.TradeOrderService;
import org.springframework.stereotype.Service;

@Service
public class TradeOrderServiceImpl extends ServiceImpl<TradeOrderMapper, TradeOrder> implements TradeOrderService {
}