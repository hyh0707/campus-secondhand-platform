package com.example.secondhand.controller;

import com.example.secondhand.common.Result;
import com.example.secondhand.config.AuthException;
import com.example.secondhand.dto.AdminLoginDTO;
import com.example.secondhand.service.AdminService;
import com.example.secondhand.utils.UserContext;
import com.example.secondhand.vo.AdminLoginVO;
import com.example.secondhand.vo.AdminProfileVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@Valid @RequestBody AdminLoginDTO dto) {
        AdminLoginVO vo = adminService.login(dto);
        return Result.success(vo);
    }

    /**
     * 获取当前管理员信息
     */
    @GetMapping("/profile")
    public Result<AdminProfileVO> profile() {
        // 防御性检查：非 admin 用户拒绝访问
        if (!"admin".equals(UserContext.getUserType())) {
            throw new AuthException(403, "无权限访问");
        }
        Long adminId = UserContext.getUserId();
        AdminProfileVO vo = adminService.getProfile(adminId);
        return Result.success(vo);
    }
}