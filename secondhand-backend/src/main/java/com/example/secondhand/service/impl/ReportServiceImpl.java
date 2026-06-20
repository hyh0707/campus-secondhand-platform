package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.secondhand.entity.Report;
import com.example.secondhand.mapper.ReportMapper;
import com.example.secondhand.service.ReportService;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {
}