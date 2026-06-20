package com.example.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.secondhand.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}