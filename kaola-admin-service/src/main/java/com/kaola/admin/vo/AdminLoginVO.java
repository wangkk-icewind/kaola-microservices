package com.kaola.admin.vo;

import lombok.Data;
import java.util.List;

/**
 * 管理员登录返回VO
 *
 * @author Kaola Team
 */
@Data
public class AdminLoginVO {

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 关联门店ID(门店管理员专用，NULL表示可管理所有门店)
     */
    private Long storeId;

    /**
     * 关联门店名称
     */
    private String storeName;

    /**
     * 权限列表
     */
    private List<String> permissions;
}
