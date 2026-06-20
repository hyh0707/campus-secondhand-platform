package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.secondhand.entity.Favorite;
import com.example.secondhand.mapper.FavoriteMapper;
import com.example.secondhand.service.FavoriteService;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {
}