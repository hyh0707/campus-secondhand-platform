package com.example.secondhand.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.secondhand.dto.UserLoginDTO;
import com.example.secondhand.dto.UserRegisterDTO;
import com.example.secondhand.dto.UserUpdateDTO;
import com.example.secondhand.entity.User;
import com.example.secondhand.vo.UserLoginVO;
import com.example.secondhand.vo.UserProfileVO;

public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    UserProfileVO register(UserRegisterDTO dto);

    /**
     * 用户登录
     */
    UserLoginVO login(UserLoginDTO dto);

    /**
     * 获取用户信息
     */
    UserProfileVO getProfile(Long userId);

    /**
     * 更新用户信息
     */
    UserProfileVO updateProfile(Long userId, UserUpdateDTO dto);
}