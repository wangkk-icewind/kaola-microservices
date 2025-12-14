package com.kaola.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kaola.user.mapper.AdminUserMapper;
import com.kaola.user.model.entity.AdminUser;
import com.kaola.user.service.AdminUserService;
import org.springframework.stereotype.Service;

/**
 * 管理员用户服务实现
 *
 * @author Kaola Team
 */
@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {
}
