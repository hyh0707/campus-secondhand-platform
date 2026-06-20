package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.secondhand.entity.Goods;
import com.example.secondhand.mapper.GoodsMapper;
import com.example.secondhand.service.GoodsService;
import org.springframework.stereotype.Service;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {
}