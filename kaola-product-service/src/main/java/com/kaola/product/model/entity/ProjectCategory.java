package com.kaola.product.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.product.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目分类/症状分类实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_project_category")
public class ProjectCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分类名称
     */
    @TableField("name")
    private String name;

    /**
     * 分类图标URL
     */
    @TableField("icon")
    private String icon;

    /**
     * 排序值 (越小越靠前)
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 状态 (0-禁用 1-启用)
     */
    @TableField("status")
    private Integer status;
}
