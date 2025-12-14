package com.kaola.product.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目分类实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_project_category")
public class ProjectCategory extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @TableField("name")
    private String name;
    
    @TableField("icon")
    private String icon;
    
    @TableField("sort")
    private Integer sort;

    @TableField("status")
    private Integer status;
}
