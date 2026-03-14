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
    private BigDecimal masseurLevelMultiplier;

    @Schema(description = "时段系数", example = "1.20")
    private BigDecimal timeSlotMultiplier;

    @Schema(description = "门店系数", example = "1.00")
    private BigDecimal storeMultiplier;

    @Schema(description = "原价（基础价格 × 技师系数 × 时段系数 × 门店系数）", example = "270.72")
    private BigDecimal originalPrice;

    @Schema(description = "加钟费用", example = "93.90")
    private BigDecimal extraPrice;

    @Schema(description = "优惠折扣金额", example = "50.00")
    private BigDecimal discountAmount;

    @Schema(description = "最终价格（原价 + 加钟费用 - 优惠折扣）", example = "314.62")
    private BigDecimal finalPrice;

    @Schema(description = "折扣说明", example = "满200减50优惠券")
    private String discountReason;
}
