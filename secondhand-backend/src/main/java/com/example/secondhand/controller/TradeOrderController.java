package com.example.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.secondhand.common.Result;
import com.example.secondhand.dto.TradeOrderCreateDTO;
import com.example.secondhand.dto.TradeOrderStatusDTO;
import com.example.secondhand.service.TradeOrderService;
import com.example.secondhand.utils.UserContext;
import com.example.secondhand.vo.TradeOrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class TradeOrderController {

    private final TradeOrderService tradeOrderService;

    /**
     * 创建交易意向
     */
    @PostMapping("/create")
    public Result<Void> create(@Valid @RequestBody TradeOrderCreateDTO dto) {
        Long userId = UserContext.getUserId();
        tradeOrderService.createOrder(userId, dto);
        return Result.success();
    }

    /**
     * 更新交易状态
     */
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody TradeOrderStatusDTO dto) {
        Long userId = UserContext.getUserId();
        tradeOrderService.updateStatus(id, userId, dto.getStatus());
        return Result.success();
    }

    /**
     * 我的购买
     */
    @GetMapping("/my-buy")
    public Result<Page<TradeOrderVO>> myBuy(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        Long userId = UserContext.getUserId();
        Page<TradeOrderVO> page = tradeOrderService.myBuy(userId, pageNum, pageSize, status);
        return Result.success(page);
    }

    /**
     * 我的出售
     */
    @GetMapping("/my-sell")
    public Result<Page<TradeOrderVO>> mySell(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        Long userId = UserContext.getUserId();
        Page<TradeOrderVO> page = tradeOrderService.mySell(userId, pageNum, pageSize, status);
        return Result.success(page);
    }
}