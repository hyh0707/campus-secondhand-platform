package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.secondhand.entity.BrowseHistory;
import com.example.secondhand.mapper.BrowseHistoryMapper;
import com.example.secondhand.service.BrowseHistoryService;
import org.springframework.stereotype.Service;

@Service
public class BrowseHistoryServiceImpl extends ServiceImpl<BrowseHistoryMapper, BrowseHistory> implements BrowseHistoryService {
}