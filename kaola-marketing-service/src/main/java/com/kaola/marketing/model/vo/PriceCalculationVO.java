package com.kaola.marketing.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 价格计算响应VO
 *
 * @author Kaola Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "价格计算响应")
public class PriceCalculationVO {

    @Schema(description = "基础价格", example = "188.00")
    private BigDecimal basePrice;

    @Schema(description = "技师等级系数", example = "1.20")
    private BigDecimal levelMultiplier;

    @Schema(description = "时段系数", example = "1.20")
    private BigDecimal timeSlotMultiplier;

    @Schema(description = "门店系数", example = "1.00")
    private BigDecimal storeMultiplier;

    @Schema(description = "原价（项目基础价格，划线价，不含任何系数）", example = "198.00")
    private BigDecimal originalPrice;

    @Schema(description = "服务价格（基础价格 × 所有系数）", example = "237.60")
    private BigDecimal servicePrice;

    @Schema(description = "加钟费用", example = "93.90")
    private BigDecimal extraPrice;

    @Schema(description = "优惠折扣金额", example = "50.00")
    private BigDecimal discountAmount;

    @Schema(description = "最终价格（原价 + 加钟费用 - 优惠折扣）", example = "314.62")
    private BigDecimal finalPrice;

    @Schema(description = "折扣说明", example = "满200减50优惠券")
    private String discountReason;

    @Schema(description = "触发的促销活动名称", example = "新客立减50元")
    private String promotionName;

    @Schema(description = "触发的促销活动类型（1满减/2折扣/4新客立减/5新客折扣）", example = "4")
    private Integer promotionType;

    @Schema(description = "基础加钟单价（元/分钟，不含倍率系数）", example = "1.45")
    private BigDecimal extraPricePerMinute;

    @Schema(description = "项目时长（分钟）", example = "60")
    private Integer duration;
}
