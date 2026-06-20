package com.example.secondhand.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUserStatusDTO {

    @NotNull(message = "状态不能为空")
    private Integer status;
}