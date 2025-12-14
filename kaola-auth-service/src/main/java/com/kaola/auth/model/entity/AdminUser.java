package com.kaola.auth.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员用户实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_admin_user")
public class AdminUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private Long roleId;

    /**
     * 关联门店ID(门店管理员专用，NULL表示可管理所有门店)
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 状态 (0-禁用 1-正常)
     */
    @TableField("status")
    private Integer status;
}
