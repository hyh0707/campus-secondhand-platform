package com.example.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("admin")
public class Admin {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    /** 角色：admin-超级管理员，editor-内容编辑 */
    private String role;

    /** 状态：0-禁用，1-正常 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}