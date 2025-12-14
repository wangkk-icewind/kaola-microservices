package com.kaola.auth.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员角色实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_admin_role")
public class AdminRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @TableField("name")
    private String name;

    /**
     * 角色描述
     */
    @TableField("description")
    private String description;

    /**
     * 权限列表 (JSON数组)
     */
    @TableField("permissions")
    private String permissions;

    /**
     * 状态 (0-禁用 1-正常)
     */
    @TableField("status")
    private Integer status;
}
