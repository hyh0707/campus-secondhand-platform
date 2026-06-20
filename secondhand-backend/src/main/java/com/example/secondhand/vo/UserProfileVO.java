package com.example.secondhand.vo;

import lombok.Data;

@Data
public class UserProfileVO {

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    private String phone;

    private String email;

    private Integer status;
}