package com.example.secondhand.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.secondhand.dto.AdminLoginDTO;
import com.example.secondhand.entity.Admin;
import com.example.secondhand.vo.AdminLoginVO;
import com.example.secondhand.vo.AdminProfileVO;

public interface AdminService extends IService<Admin> {

    /**
     * 管理员登录
     */
    AdminLoginVO login(AdminLoginDTO dto);

    /**
     * 获取管理员信息
     */
    AdminProfileVO getProfile(Long adminId);
}