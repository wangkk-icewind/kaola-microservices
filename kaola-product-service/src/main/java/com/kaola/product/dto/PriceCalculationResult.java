package com.kaola.product.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 价格计算结果DTO
 *
 * @author Kaola Team
 */
@Data
public class PriceCalculationResult {

    /**
     * 项目基础价格
     */
    private BigDecimal basePrice;

    /**
     * 技师等级系数
     */
    private BigDecimal levelMultiplier;

    /**
     * 时段系数
     */
    private BigDecimal timeSlotMultiplier;

    /**
     * 门店系数
     */
    private BigDecimal storeMultiplier;

    /**
     * 服务时长（分钟）
     */
    private Integer duration;

    /**
     * 服务价格（基础价格 × 系数）
     */
    private BigDecimal servicePrice;

    /**
     * 加钟时长（分钟）
     */
    private Integer extraDuration;

    /**
     * 加钟单价（每分钟）
     */
    private BigDecimal extraPricePerMinute;

    /**
     * 加钟费用
     */
    private BigDecimal extraCharge;

    /**
     * 小计（服务价格 + 加钟费用）
     */
    private BigDecimal subtotal;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 最终价格
     */
    private BigDecimal finalPrice;

    /**
     * 价格说明
     */
    private String priceDescription;
}
