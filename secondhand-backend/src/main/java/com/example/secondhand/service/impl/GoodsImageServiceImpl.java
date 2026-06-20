package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.secondhand.entity.GoodsImage;
import com.example.secondhand.mapper.GoodsImageMapper;
import com.example.secondhand.service.GoodsImageService;
import org.springframework.stereotype.Service;

@Service
public class GoodsImageServiceImpl extends ServiceImpl<GoodsImageMapper, GoodsImage> implements GoodsImageService {
}