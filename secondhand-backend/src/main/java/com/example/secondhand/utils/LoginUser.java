package com.example.secondhand.utils;

import lombok.Data;

@Data
public class LoginUser {

    private Long userId;
    private String username;
    private String userType;  // user / admin
    private String role;      // admin 角色
}