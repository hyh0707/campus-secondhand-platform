package com.example.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.secondhand.common.Result;
import com.example.secondhand.config.AuthException;
import com.example.secondhand.dto.AdminAuditDTO;
import com.example.secondhand.dto.AdminDemandQueryDTO;
import com.example.secondhand.dto.AdminGoodsQueryDTO;
import com.example.secondhand.dto.AdminLoginDTO;
import com.example.secondhand.dto.AdminUserQueryDTO;
import com.example.secondhand.dto.AdminUserStatusDTO;
import com.example.secondhand.service.AdminDemandService;
import com.example.secondhand.service.AdminGoodsService;
import com.example.secondhand.service.AdminService;
import com.example.secondhand.service.AdminUserService;
import com.example.secondhand.utils.UserContext;
import com.example.secondhand.vo.AdminDemandVO;
import com.example.secondhand.vo.AdminGoodsVO;
import com.example.secondhand.vo.AdminLoginVO;
import com.example.secondhand.vo.AdminProfileVO;
import com.example.secondhand.vo.AdminUserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminGoodsService adminGoodsService;
    private final AdminDemandService adminDemandService;
    private final AdminUserService adminUserService;

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
        requireAdmin();
        Long adminId = UserContext.getUserId();
        AdminProfileVO vo = adminService.getProfile(adminId);
        return Result.success(vo);
    }

    // ==================== 商品管理 ====================

    /**
     * 管理员商品列表（需管理员登录）
     */
    @GetMapping("/goods/list")
    public Result<Page<AdminGoodsVO>> goodsList(AdminGoodsQueryDTO query) {
        requireAdmin();
        Page<AdminGoodsVO> page = adminGoodsService.listGoods(query);
        return Result.success(page);
    }

    /**
     * 审核商品（需管理员登录）
     */
    @PutMapping("/goods/audit/{id}")
    public Result<Void> auditGoods(@PathVariable Long id, @Valid @RequestBody AdminAuditDTO dto) {
        requireAdmin();
        adminGoodsService.auditGoods(id, dto);
        return Result.success();
    }

    /**
     * 下架商品（需管理员登录）
     */
    @PutMapping("/goods/offline/{id}")
    public Result<Void> offlineGoods(@PathVariable Long id, @RequestBody AdminAuditDTO dto) {
        requireAdmin();
        adminGoodsService.offlineGoods(id, dto.getReason());
        return Result.success();
    }

    // ==================== 求购管理 ====================

    /**
     * 管理员求购列表（需管理员登录）
     */
    @GetMapping("/demand/list")
    public Result<Page<AdminDemandVO>> demandList(AdminDemandQueryDTO query) {
        requireAdmin();
        Page<AdminDemandVO> page = adminDemandService.listDemands(query);
        return Result.success(page);
    }

    /**
     * 审核求购（需管理员登录）
     */
    @PutMapping("/demand/audit/{id}")
    public Result<Void> auditDemand(@PathVariable Long id, @Valid @RequestBody AdminAuditDTO dto) {
        requireAdmin();
        adminDemandService.auditDemand(id, dto);
        return Result.success();
    }

    // ==================== 用户管理 ====================

    /**
     * 管理员用户列表（需管理员登录）
     */
    @GetMapping("/user/list")
    public Result<Page<AdminUserVO>> userList(AdminUserQueryDTO query) {
        requireAdmin();
        Page<AdminUserVO> page = adminUserService.listUsers(query);
        return Result.success(page);
    }

    /**
     * 禁用/启用用户（需管理员登录）
     */
    @PutMapping("/user/status/{id}")
    public Result<Void> updateUserStatus(@PathVariable Long id, @Valid @RequestBody AdminUserStatusDTO dto) {
        requireAdmin();
        adminUserService.updateStatus(id, dto.getStatus());
        return Result.success();
    }

    // ==================== 私有方法 ====================

    private void requireAdmin() {
        if (!"admin".equals(UserContext.getUserType())) {
            throw new AuthException(403, "无权限访问");
        }
    }
}