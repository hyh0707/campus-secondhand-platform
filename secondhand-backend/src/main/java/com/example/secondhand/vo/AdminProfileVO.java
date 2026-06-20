package com.example.secondhand.vo;

import lombok.Data;

@Data
public class AdminProfileVO {

    private Long id;

    private String username;

    private String nickname;

    private String role;

    private Integer status;
}