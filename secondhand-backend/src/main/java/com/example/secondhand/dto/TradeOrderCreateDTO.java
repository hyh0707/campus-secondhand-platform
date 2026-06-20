package com.example.secondhand.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TradeOrderCreateDTO {

    @NotNull(message = "商品ID不能为空")
    private Long goodsId;

    @NotBlank(message = "留言不能为空")
    private String message;
}