package com.example.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.secondhand.entity.SearchHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {
}