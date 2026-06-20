package com.example.secondhand.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserVO {

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    private String phone;

    private String email;

    /** 状态：0-禁用，1-正常 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}