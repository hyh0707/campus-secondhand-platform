package com.example.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.secondhand.common.Result;
import com.example.secondhand.dto.FavoriteAddDTO;
import com.example.secondhand.service.FavoriteService;
import com.example.secondhand.utils.UserContext;
import com.example.secondhand.vo.FavoriteGoodsVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * 添加收藏
     */
    @PostMapping("/add")
    public Result<Void> add(@Valid @RequestBody FavoriteAddDTO dto) {
        Long userId = UserContext.getUserId();
        favoriteService.addFavorite(userId, dto.getGoodsId());
        return Result.success();
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/delete/{goodsId}")
    public Result<Void> delete(@PathVariable Long goodsId) {
        Long userId = UserContext.getUserId();
        favoriteService.removeFavorite(userId, goodsId);
        return Result.success();
    }

    /**
     * 收藏列表
     */
    @GetMapping("/list")
    public Result<Page<FavoriteGoodsVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        Page<FavoriteGoodsVO> page = favoriteService.listFavorites(userId, pageNum, pageSize);
        return Result.success(page);
    }
}