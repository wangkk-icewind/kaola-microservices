package com.kaola.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kaola.admin.entity.AdminRole;

import java.util.List;

/**
 * 管理员角色服务接口
 *
 * @author Kaola Team
 */
public interface AdminRoleService extends IService<AdminRole> {

    /**
     * 获取所有启用的角色
     *
     * @return 角色列表
     */
    List<AdminRole> getAllEnabled();
}
