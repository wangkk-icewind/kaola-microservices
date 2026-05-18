package com.kaola.marketing.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 价格计算请求DTO
 *
 * @author Kaola Team
 */
@Data
@Schema(description = "价格计算请求")
public class PriceCalculationRequest {

    @Schema(description = "项目ID", required = true, example = "1")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @Schema(description = "技师等级：1-初级 2-中级 3-高级 4-专家", required = true, example = "2")
    @NotNull(message = "技师等级不能为空")
    private Integer masseurLevel;

    @Schema(description = "门店ID", required = true, example = "1")
    @NotNull(message = "门店ID不能为空")
    private Long storeId;

    @Schema(description = "预约时间", required = true, example = "2026-01-26T14:00:00")
    @NotNull(message = "预约时间不能为空")
    private LocalDateTime appointmentTime;

    @Schema(description = "加钟分钟数", example = "30")
    private Integer extraMinutes;

    @Schema(description = "优惠券ID", example = "1")
    private Long couponId;

    @Schema(description = "是否新客户（首次下单享新客优惠）", example = "false")
    private Boolean isNewCustomer;
}
