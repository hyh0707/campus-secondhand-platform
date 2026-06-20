package com.example.secondhand.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminAuditDTO {

    @NotBlank(message = "审核状态不能为空")
    private String status;

    private String reason;
}