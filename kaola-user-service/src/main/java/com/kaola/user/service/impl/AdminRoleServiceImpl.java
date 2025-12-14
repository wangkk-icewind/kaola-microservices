package com.kaola.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kaola.user.mapper.AdminRoleMapper;
import com.kaola.user.model.entity.AdminRole;
import com.kaola.user.service.AdminRoleService;
import org.springframework.stereotype.Service;

/**
 * 管理员角色服务实现
 *
 * @author Kaola Team
 */
@Service
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements AdminRoleService {
}
