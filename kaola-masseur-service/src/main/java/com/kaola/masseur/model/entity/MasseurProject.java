package com.kaola.masseur.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 技师-服务项目关联实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_masseur_project")
public class MasseurProject extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 技师ID
     */
    @TableField("masseur_id")
    private Long masseurId;

    /**
     * 服务项目ID
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 技师个人定价（可覆盖项目默认价格）
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 状态：0-禁用 1-启用
     */
    @TableField("status")
    private Integer status;
}
