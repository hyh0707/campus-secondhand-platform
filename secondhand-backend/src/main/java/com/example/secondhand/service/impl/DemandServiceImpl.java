package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.secondhand.common.BusinessException;
import com.example.secondhand.common.ResultCode;
import com.example.secondhand.dto.DemandAddDTO;
import com.example.secondhand.dto.DemandQueryDTO;
import com.example.secondhand.dto.DemandUpdateDTO;
import com.example.secondhand.entity.Demand;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.DemandMapper;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.service.DemandService;
import com.example.secondhand.vo.DemandDetailVO;
import com.example.secondhand.vo.DemandListVO;
import com.example.secondhand.vo.DemandUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandServiceImpl extends ServiceImpl<DemandMapper, Demand> implements DemandService {

    private final UserMapper userMapper;

    @Override
    public void addDemand(Long userId, DemandAddDTO dto) {
        Demand demand = new Demand();
        demand.setUserId(userId);
        demand.setCategoryId(dto.getCategoryId());
        demand.setTitle(dto.getTitle());
        demand.setDescription(dto.getDescription());
        demand.setMinPrice(dto.getMinPrice());
        demand.setMaxPrice(dto.getMaxPrice());
        demand.setExpectedCondition(dto.getExpectedCondition());
        demand.setExpectedLocation(dto.getExpectedLocation());
        demand.setKeywords(StringUtils.hasText(dto.getKeywords())
                ? dto.getKeywords()
                : extractKeywords(dto.getTitle(), dto.getDescription()));
        demand.setStatus("pending");
        save(demand);
    }

    @Override
    public Page<DemandListVO> listDemands(DemandQueryDTO query) {
        LambdaQueryWrapper<Demand> wrapper = new LambdaQueryWrapper<>();

        // 默认只展示审核通过的
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Demand::getStatus, query.getStatus());
        } else {
            wrapper.eq(Demand::getStatus, "approved");
        }

        // 关键词搜索 title / description / keywords
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w
                    .like(Demand::getTitle, query.getKeyword())
                    .or()
                    .like(Demand::getDescription, query.getKeyword())
                    .or()
                    .like(Demand::getKeywords, query.getKeyword()));
        }

        if (query.getCategoryId() != null) {
            wrapper.eq(Demand::getCategoryId, query.getCategoryId());
        }
        // 预算区间（minPrice >= 查询的 minPrice，maxPrice <= 查询的 maxPrice）
        if (query.getMinPrice() != null) {
            wrapper.ge(Demand::getMinPrice, query.getMinPrice());
        }
        if (query.getMaxPrice() != null) {
            wrapper.le(Demand::getMaxPrice, query.getMaxPrice());
        }

        wrapper.orderByDesc(Demand::getCreateTime);

        Page<Demand> page = page(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        return buildListVOPage(page);
    }

    @Override
    public DemandDetailVO getDetail(Long id) {
        Demand demand = getById(id);
        if (demand == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        DemandDetailVO vo = new DemandDetailVO();
        vo.setId(demand.getId());
        vo.setCategoryId(demand.getCategoryId());
        vo.setTitle(demand.getTitle());
        vo.setDescription(demand.getDescription());
        vo.setMinPrice(demand.getMinPrice());
        vo.setMaxPrice(demand.getMaxPrice());
        vo.setExpectedCondition(demand.getExpectedCondition());
        vo.setExpectedLocation(demand.getExpectedLocation());
        vo.setKeywords(demand.getKeywords());
        vo.setStatus(demand.getStatus());
        vo.setCreateTime(demand.getCreateTime());
        vo.setUpdateTime(demand.getUpdateTime());

        // 卖家信息
        User seller = userMapper.selectById(demand.getUserId());
        if (seller != null) {
            DemandUserVO userVO = new DemandUserVO();
            userVO.setId(seller.getId());
            userVO.setUsername(seller.getUsername());
            userVO.setNickname(seller.getNickname());
            userVO.setAvatar(seller.getAvatar());
            vo.setUser(userVO);
        }

        return vo;
    }

    @Override
    public void updateDemand(Long demandId, Long userId, DemandUpdateDTO dto) {
        Demand demand = getById(demandId);
        if (demand == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!demand.getUserId().equals(userId)) {
            throw new BusinessException("只能修改自己发布的求购");
        }
        if ("closed".equals(demand.getStatus())) {
            throw new BusinessException("已关闭的求购不可修改");
        }

        if (dto.getCategoryId() != null) {
            demand.setCategoryId(dto.getCategoryId());
        }
        if (dto.getTitle() != null) {
            demand.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            demand.setDescription(dto.getDescription());
        }
        if (dto.getMinPrice() != null) {
            demand.setMinPrice(dto.getMinPrice());
        }
        if (dto.getMaxPrice() != null) {
            demand.setMaxPrice(dto.getMaxPrice());
        }
        if (dto.getExpectedCondition() != null) {
            demand.setExpectedCondition(dto.getExpectedCondition());
        }
        if (dto.getExpectedLocation() != null) {
            demand.setExpectedLocation(dto.getExpectedLocation());
        }
        if (dto.getKeywords() != null) {
            demand.setKeywords(dto.getKeywords());
        }
        // 修改后变更为待审核
        demand.setStatus("pending");
        updateById(demand);
    }

    @Override
    public void deleteDemand(Long demandId, Long userId) {
        Demand demand = getById(demandId);
        if (demand == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!demand.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己发布的求购");
        }

        demand.setStatus("closed");
        updateById(demand);
    }

    @Override
    public Page<DemandListVO> myDemands(Long userId, Integer pageNum, Integer pageSize, String status) {
        LambdaQueryWrapper<Demand> wrapper = new LambdaQueryWrapper<Demand>()
                .eq(Demand::getUserId, userId);

        if (StringUtils.hasText(status)) {
            wrapper.eq(Demand::getStatus, status);
        }

        wrapper.orderByDesc(Demand::getCreateTime);

        Page<Demand> page = page(new Page<>(pageNum, pageSize), wrapper);
        return buildListVOPage(page);
    }

    // ==================== 私有方法 ====================

    /**
     * 从标题和描述中提取简单关键词
     */
    private String extractKeywords(String title, String description) {
        Set<String> words = new LinkedHashSet<>();
        String text = title + " " + (description != null ? description : "");
        // 按非中文字符和非字母数字分割
        for (String token : text.split("[^\\u4e00-\\u9fa5a-zA-Z0-9]+")) {
            String word = token.trim();
            if (word.length() >= 2 && word.length() <= 20) {
                words.add(word);
            }
        }
        // 取前 10 个
        return words.stream().limit(10).collect(Collectors.joining(","));
    }

    /**
     * 构建列表分页 VO（批量查用户信息）
     */
    private Page<DemandListVO> buildListVOPage(Page<Demand> page) {
        // 批量查询用户
        List<Long> userIds = page.getRecords().stream()
                .map(Demand::getUserId)
                .distinct()
                .collect(Collectors.toList());
        final Map<Long, User> userMap;
        if (userIds.isEmpty()) {
            userMap = Collections.emptyMap();
        } else {
            userMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        }

        Page<DemandListVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<DemandListVO> voList = page.getRecords().stream().map(d -> {
            DemandListVO vo = new DemandListVO();
            vo.setId(d.getId());
            vo.setCategoryId(d.getCategoryId());
            vo.setTitle(d.getTitle());
            vo.setMinPrice(d.getMinPrice());
            vo.setMaxPrice(d.getMaxPrice());
            vo.setExpectedCondition(d.getExpectedCondition());
            vo.setExpectedLocation(d.getExpectedLocation());
            vo.setKeywords(d.getKeywords());
            vo.setStatus(d.getStatus());
            vo.setCreateTime(d.getCreateTime());

            User u = userMap.get(d.getUserId());
            if (u != null) {
                DemandUserVO userVO = new DemandUserVO();
                userVO.setId(u.getId());
                userVO.setUsername(u.getUsername());
                userVO.setNickname(u.getNickname());
                userVO.setAvatar(u.getAvatar());
                vo.setUser(userVO);
            }
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }
}