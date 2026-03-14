package com.kaola.product.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 价格计算结果VO
 */
@Data
public class PriceCalculationVO {

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 基础价格
     */
    private BigDecimal basePrice;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 最终价格
     */
    private BigDecimal finalPrice;

    /**
     * 实际价格
     */
    private BigDecimal actualPrice;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 优惠原因/说明
     */
    private String discountReason;

    /**
     * 技师等级：1-初级 2-中级 3-高级 4-专家
     */
    private Integer masseurLevel;

    /**
     * 时段类型：1-普通时段 2-高峰时段 3-夜间时段
     */
    private Integer timeSlotType;
}
