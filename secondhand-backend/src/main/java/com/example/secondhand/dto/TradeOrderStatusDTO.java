package com.example.secondhand.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TradeOrderStatusDTO {

    @NotBlank(message = "状态不能为空")
    private String status;
}