package com.kaola.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kaola.auth.mapper.AdminRoleMapper;
import com.kaola.auth.model.entity.AdminRole;
import com.kaola.auth.service.AdminRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理员角色服务实现
 *
 * @author Kaola Team
 */
@Service
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements AdminRoleService {

    @Override
    public List<AdminRole> getAllEnabled() {
        return list(new LambdaQueryWrapper<AdminRole>()
            .eq(AdminRole::getStatus, 1)
            .orderByAsc(AdminRole::getId));
    }
}
