package com.example.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.secondhand.common.Result;
import com.example.secondhand.dto.DemandAddDTO;
import com.example.secondhand.dto.DemandQueryDTO;
import com.example.secondhand.dto.DemandUpdateDTO;
import com.example.secondhand.service.DemandService;
import com.example.secondhand.utils.UserContext;
import com.example.secondhand.vo.DemandDetailVO;
import com.example.secondhand.vo.DemandListVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demand")
@RequiredArgsConstructor
public class DemandController {

    private final DemandService demandService;

    /**
     * 求购列表（匿名可访问）
     */
    @GetMapping("/list")
    public Result<Page<DemandListVO>> list(DemandQueryDTO query) {
        Page<DemandListVO> page = demandService.listDemands(query);
        return Result.success(page);
    }

    /**
     * 求购详情（匿名可访问）
     */
    @GetMapping("/detail/{id}")
    public Result<DemandDetailVO> detail(@PathVariable Long id) {
        DemandDetailVO vo = demandService.getDetail(id);
        return Result.success(vo);
    }

    /**
     * 发布求购（需登录）
     */
    @PostMapping("/add")
    public Result<Void> add(@Valid @RequestBody DemandAddDTO dto) {
        Long userId = UserContext.getUserId();
        demandService.addDemand(userId, dto);
        return Result.success();
    }

    /**
     * 修改求购（需登录，只能修改自己的）
     */
    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DemandUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        demandService.updateDemand(id, userId, dto);
        return Result.success();
    }

    /**
     * 删除求购（需登录，只能删除自己的）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        demandService.deleteDemand(id, userId);
        return Result.success();
    }

    /**
     * 我的求购列表（需登录）
     */
    @GetMapping("/my")
    public Result<Page<DemandListVO>> my(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        Long userId = UserContext.getUserId();
        Page<DemandListVO> page = demandService.myDemands(userId, pageNum, pageSize, status);
        return Result.success(page);
    }
}