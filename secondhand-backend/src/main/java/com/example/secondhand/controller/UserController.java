package com.example.secondhand.controller;

import com.example.secondhand.common.Result;
import com.example.secondhand.dto.UserLoginDTO;
import com.example.secondhand.dto.UserRegisterDTO;
import com.example.secondhand.dto.UserUpdateDTO;
import com.example.secondhand.service.UserService;
import com.example.secondhand.utils.UserContext;
import com.example.secondhand.vo.UserLoginVO;
import com.example.secondhand.vo.UserProfileVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserProfileVO> register(@Valid @RequestBody UserRegisterDTO dto) {
        UserProfileVO vo = userService.register(dto);
        return Result.success(vo);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        UserLoginVO vo = userService.login(dto);
        return Result.success(vo);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<UserProfileVO> profile() {
        Long userId = UserContext.getUserId();
        UserProfileVO vo = userService.getProfile(userId);
        return Result.success(vo);
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/profile")
    public Result<UserProfileVO> updateProfile(@RequestBody UserUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        UserProfileVO vo = userService.updateProfile(userId, dto);
        return Result.success(vo);
    }
}