package com.kaola.product.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 价格计算请求DTO
 *
 * @author Kaola Team
 */
@Data
public class PriceCalculationRequest {

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 技师ID
     */
    private Long masseurId;

    /**
     * 技师等级 (如果不传masseurId，则必须传技师等级)
     */
    private Integer masseurLevel;

    /**
     * 服务时长（分钟）
     */
    private Integer duration;

    /**
     * 加钟时长（分钟）
     */
    private Integer extraDuration;

    /**
     * 预约时间（用于计算时段系数）
     */
    private LocalDateTime appointmentTime;

    /**
     * 优惠券ID（可选）
     */
    private Long couponId;

    /**
     * 促销活动ID（可选）
     */
    private Long promotionId;
}
