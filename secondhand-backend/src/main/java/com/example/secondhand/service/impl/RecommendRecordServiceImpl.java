package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.secondhand.entity.RecommendRecord;
import com.example.secondhand.mapper.RecommendRecordMapper;
import com.example.secondhand.service.RecommendRecordService;
import org.springframework.stereotype.Service;

@Service
public class RecommendRecordServiceImpl extends ServiceImpl<RecommendRecordMapper, RecommendRecord> implements RecommendRecordService {
}