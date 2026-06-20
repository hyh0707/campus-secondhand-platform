package com.example.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.secondhand.common.Result;
import com.example.secondhand.dto.GoodsAddDTO;
import com.example.secondhand.dto.GoodsQueryDTO;
import com.example.secondhand.dto.GoodsUpdateDTO;
import com.example.secondhand.service.GoodsService;
import com.example.secondhand.utils.UserContext;
import com.example.secondhand.vo.GoodsDetailVO;
import com.example.secondhand.vo.GoodsListVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    /**
     * 商品列表（匿名可访问，已登录时记录搜索历史）
     */
    @GetMapping("/list")
    public Result<Page<GoodsListVO>> list(GoodsQueryDTO query) {
        Long userId = UserContext.getUserId();
        Page<GoodsListVO> page = goodsService.listGoods(query, userId);
        return Result.success(page);
    }

    /**
     * 商品详情（匿名可访问，已登录时记录浏览历史）
     */
    @GetMapping("/detail/{id}")
    public Result<GoodsDetailVO> detail(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        GoodsDetailVO vo = goodsService.getDetail(id, userId);
        return Result.success(vo);
    }

    /**
     * 发布商品（需登录）
     */
    @PostMapping("/add")
    public Result<Void> add(@Valid @RequestBody GoodsAddDTO dto) {
        Long userId = UserContext.getUserId();
        goodsService.addGoods(userId, dto);
        return Result.success();
    }

    /**
     * 修改商品（需登录，只能修改自己的）
     */
    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody GoodsUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        goodsService.updateGoods(id, userId, dto);
        return Result.success();
    }

    /**
     * 删除商品（需登录，只能删除自己的）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        goodsService.deleteGoods(id, userId);
        return Result.success();
    }

    /**
     * 我的商品列表（需登录）
     */
    @GetMapping("/my")
    public Result<Page<GoodsListVO>> my(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        Long userId = UserContext.getUserId();
        Page<GoodsListVO> page = goodsService.myGoods(userId, pageNum, pageSize, status);
        return Result.success(page);
    }
}