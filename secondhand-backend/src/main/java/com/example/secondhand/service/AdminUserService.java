package com.example.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.secondhand.dto.AdminUserQueryDTO;
import com.example.secondhand.vo.AdminUserVO;

public interface AdminUserService {

    /**
     * 管理员用户列表
     */
    Page<AdminUserVO> listUsers(AdminUserQueryDTO query);

    /**
     * 禁用/启用用户
     */
    void updateStatus(Long userId, Integer status);
}