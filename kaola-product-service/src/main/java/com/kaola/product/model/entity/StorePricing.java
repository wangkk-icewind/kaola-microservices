package com.kaola.product.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.product.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 门店价格系数配置实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_store_pricing")
public class StorePricing extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 门店ID
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 门店名称 (冗余字段，便于查询)
     */
    @TableField("store_name")
    private String storeName;

    /**
     * 价格系数
     */
    @TableField("multiplier")
    private BigDecimal multiplier;

    /**
     * 描述说明 (如"旗舰店"、"郊区店")
     */
    @TableField("description")
    private String description;

    /**
     * 状态 (0-禁用 1-启用)
     */
    @TableField("status")
    private Integer status;
}
