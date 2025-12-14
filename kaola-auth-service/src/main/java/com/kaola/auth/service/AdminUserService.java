package com.kaola.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kaola.auth.model.entity.AdminUser;
import com.kaola.auth.model.vo.AdminLoginVO;

/**
 * 管理员用户服务接口
 *
 * @author Kaola Team
 */
public interface AdminUserService extends IService<AdminUser> {

    /**
     * 管理员登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    AdminLoginVO login(String username, String password);

    /**
     * 根据用户名查询管理员
     *
     * @param username 用户名
     * @return 管理员信息
     */
    AdminUser getByUsername(String username);
}
