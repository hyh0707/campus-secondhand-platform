package com.example.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.secondhand.entity.Demand;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemandMapper extends BaseMapper<Demand> {
}