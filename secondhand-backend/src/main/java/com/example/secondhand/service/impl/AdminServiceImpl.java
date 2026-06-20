package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.secondhand.common.BusinessException;
import com.example.secondhand.common.ResultCode;
import com.example.secondhand.dto.AdminLoginDTO;
import com.example.secondhand.entity.Admin;
import com.example.secondhand.mapper.AdminMapper;
import com.example.secondhand.service.AdminService;
import com.example.secondhand.utils.JwtUtils;
import com.example.secondhand.utils.PasswordUtils;
import com.example.secondhand.vo.AdminLoginVO;
import com.example.secondhand.vo.AdminProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    private final PasswordUtils passwordUtils;
    private final JwtUtils jwtUtils;

    @Override
    public AdminLoginVO login(AdminLoginDTO dto) {
        Admin admin = lambdaQuery()
                .eq(Admin::getUsername, dto.getUsername())
                .one();
        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (admin.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordUtils.matches(dto.getPassword(), admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtUtils.generateToken(admin.getId(), admin.getUsername(), "admin", admin.getRole());

        AdminLoginVO.AdminInfo adminInfo = new AdminLoginVO.AdminInfo(
                admin.getId(), admin.getUsername(), admin.getNickname(), admin.getRole());
        return new AdminLoginVO(token, adminInfo);
    }

    @Override
    public AdminProfileVO getProfile(Long adminId) {
        Admin admin = getById(adminId);
        if (admin == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        AdminProfileVO vo = new AdminProfileVO();
        vo.setId(admin.getId());
        vo.setUsername(admin.getUsername());
        vo.setNickname(admin.getNickname());
        vo.setRole(admin.getRole());
        vo.setStatus(admin.getStatus());
        return vo;
    }
}