package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.secondhand.common.BusinessException;
import com.example.secondhand.common.ResultCode;
import com.example.secondhand.dto.AdminAuditDTO;
import com.example.secondhand.dto.AdminDemandQueryDTO;
import com.example.secondhand.entity.Demand;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.DemandMapper;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.service.AdminDemandService;
import com.example.secondhand.vo.AdminDemandVO;
import com.example.secondhand.vo.SellerVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDemandServiceImpl implements AdminDemandService {

    private final DemandMapper demandMapper;
    private final UserMapper userMapper;

    @Override
    public Page<AdminDemandVO> listDemands(AdminDemandQueryDTO query) {
        LambdaQueryWrapper<Demand> wrapper = new LambdaQueryWrapper<>();

        // 状态筛选
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Demand::getStatus, query.getStatus());
        }

        // 关键词搜索（title、description、keywords）
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w
                    .like(Demand::getTitle, query.getKeyword())
                    .or()
                    .like(Demand::getDescription, query.getKeyword())
                    .or()
                    .like(Demand::getKeywords, query.getKeyword()));
        }

        // 分类筛选
        if (query.getCategoryId() != null) {
            wrapper.eq(Demand::getCategoryId, query.getCategoryId());
        }

        wrapper.orderByDesc(Demand::getCreateTime);

        Page<Demand> page = demandMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        // 批量查用户
        Set<Long> userIds = page.getRecords().stream()
                .map(Demand::getUserId)
                .collect(Collectors.toSet());
        final Map<Long, User> userMap;
        if (userIds.isEmpty()) {
            userMap = Collections.emptyMap();
        } else {
            userMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        }

        Page<AdminDemandVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<AdminDemandVO> voList = page.getRecords().stream().map(d -> {
            AdminDemandVO vo = new AdminDemandVO();
            vo.setId(d.getId());
            vo.setUserId(d.getUserId());
            vo.setCategoryId(d.getCategoryId());
            vo.setTitle(d.getTitle());
            vo.setDescription(d.getDescription());
            vo.setMinPrice(d.getMinPrice());
            vo.setMaxPrice(d.getMaxPrice());
            vo.setExpectedCondition(d.getExpectedCondition());
            vo.setExpectedLocation(d.getExpectedLocation());
            vo.setKeywords(d.getKeywords());
            vo.setStatus(d.getStatus());
            vo.setCreateTime(d.getCreateTime());
            vo.setUpdateTime(d.getUpdateTime());

            User u = userMap.get(d.getUserId());
            if (u != null) {
                SellerVO seller = new SellerVO();
                seller.setId(u.getId());
                seller.setUsername(u.getUsername());
                seller.setNickname(u.getNickname());
                seller.setAvatar(u.getAvatar());
                vo.setUser(seller);
            }
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional
    public void auditDemand(Long demandId, AdminAuditDTO dto) {
        if (!"approved".equals(dto.getStatus()) && !"rejected".equals(dto.getStatus())) {
            throw new BusinessException("审核状态只允许 approved 或 rejected");
        }

        Demand demand = demandMapper.selectById(demandId);
        if (demand == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!"pending".equals(demand.getStatus())) {
            throw new BusinessException("只能审核待审核状态的求购");
        }

        demand.setStatus(dto.getStatus());
        demandMapper.updateById(demand);
    }
}