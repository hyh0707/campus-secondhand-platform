package com.example.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.secondhand.dto.AdminAuditDTO;
import com.example.secondhand.dto.AdminDemandQueryDTO;
import com.example.secondhand.vo.AdminDemandVO;

public interface AdminDemandService {

    /**
     * 管理员求购列表
     */
    Page<AdminDemandVO> listDemands(AdminDemandQueryDTO query);

    /**
     * 审核求购
     */
    void auditDemand(Long demandId, AdminAuditDTO dto);
}