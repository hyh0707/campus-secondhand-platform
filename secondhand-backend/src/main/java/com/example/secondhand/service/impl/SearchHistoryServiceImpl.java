package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.secondhand.entity.SearchHistory;
import com.example.secondhand.mapper.SearchHistoryMapper;
import com.example.secondhand.service.SearchHistoryService;
import org.springframework.stereotype.Service;

@Service
public class SearchHistoryServiceImpl extends ServiceImpl<SearchHistoryMapper, SearchHistory> implements SearchHistoryService {
}