package com.example.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.secondhand.common.BusinessException;
import com.example.secondhand.common.ResultCode;
import com.example.secondhand.dto.AdminUserQueryDTO;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.service.AdminUserService;
import com.example.secondhand.vo.AdminUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;

    @Override
    public Page<AdminUserVO> listUsers(AdminUserQueryDTO query) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 状态筛选
        if (query.getStatus() != null) {
            wrapper.eq(User::getStatus, query.getStatus());
        }

        // 关键词搜索（username、nickname、phone、email）
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w
                    .like(User::getUsername, query.getKeyword())
                    .or()
                    .like(User::getNickname, query.getKeyword())
                    .or()
                    .like(User::getPhone, query.getKeyword())
                    .or()
                    .like(User::getEmail, query.getKeyword()));
        }

        wrapper.orderByDesc(User::getCreateTime);

        Page<User> page = userMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        Page<AdminUserVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<AdminUserVO> voList = page.getRecords().stream().map(u -> {
            AdminUserVO vo = new AdminUserVO();
            vo.setId(u.getId());
            vo.setUsername(u.getUsername());
            vo.setNickname(u.getNickname());
            vo.setAvatar(u.getAvatar());
            vo.setPhone(u.getPhone());
            vo.setEmail(u.getEmail());
            vo.setStatus(u.getStatus());
            vo.setCreateTime(u.getCreateTime());
            vo.setUpdateTime(u.getUpdateTime());
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional
    public void updateStatus(Long userId, Integer status) {
        if (status != 0 && status != 1) {
            throw new BusinessException("状态只允许 0 或 1");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        user.setStatus(status);
        userMapper.updateById(user);
    }
}