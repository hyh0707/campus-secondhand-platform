package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.secondhand.entity.Demand;
import com.example.secondhand.mapper.DemandMapper;
import com.example.secondhand.service.DemandService;
import org.springframework.stereotype.Service;

@Service
public class DemandServiceImpl extends ServiceImpl<DemandMapper, Demand> implements DemandService {
}