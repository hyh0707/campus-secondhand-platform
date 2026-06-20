package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.secondhand.entity.Admin;
import com.example.secondhand.mapper.AdminMapper;
import com.example.secondhand.service.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
}