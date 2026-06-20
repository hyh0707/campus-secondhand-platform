package com.example.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.secondhand.dto.DemandAddDTO;
import com.example.secondhand.dto.DemandQueryDTO;
import com.example.secondhand.dto.DemandUpdateDTO;
import com.example.secondhand.entity.Demand;
import com.example.secondhand.vo.DemandDetailVO;
import com.example.secondhand.vo.DemandListVO;

public interface DemandService extends IService<Demand> {

    /**
     * 发布求购
     */
    void addDemand(Long userId, DemandAddDTO dto);

    /**
     * 求购列表（分页 + 筛选）
     */
    Page<DemandListVO> listDemands(DemandQueryDTO query);

    /**
     * 求购详情
     */
    DemandDetailVO getDetail(Long id);

    /**
     * 修改求购
     */
    void updateDemand(Long demandId, Long userId, DemandUpdateDTO dto);

    /**
     * 删除求购（软删除 → closed）
     */
    void deleteDemand(Long demandId, Long userId);

    /**
     * 我的求购列表
     */
    Page<DemandListVO> myDemands(Long userId, Integer pageNum, Integer pageSize, String status);
}