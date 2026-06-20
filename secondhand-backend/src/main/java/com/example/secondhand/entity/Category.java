package com.example.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /** 父分类ID，0表示顶级分类 */
    private Long parentId;

    private String icon;

    /** 排序值，越小越靠前 */
    private Integer sort;

    /** 状态：0-禁用，1-启用 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}