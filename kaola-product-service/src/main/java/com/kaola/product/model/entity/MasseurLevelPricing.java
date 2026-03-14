package com.kaola.product.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.product.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 技师等级价格系数配置实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_masseur_level_pricing")
public class MasseurLevelPricing extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 技师等级 (1-初级 2-中级 3-高级 4-专家)
     */
    @TableField("level")
    private Integer level;

    /**
     * 等级名称
     */
    @TableField("level_name")
    private String levelName;

    /**
     * 价格系数 (如1.0表示原价，1.2表示1.2倍)
     */
    @TableField("multiplier")
    private BigDecimal multiplier;

    /**
     * 描述说明
     */
    @TableField("description")
    private String description;

    /**
     * 状态 (0-禁用 1-启用)
     */
    @TableField("status")
    private Integer status;
}
