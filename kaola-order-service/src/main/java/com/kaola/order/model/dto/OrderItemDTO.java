package com.kaola.order.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 订单项DTO
 *
 * @author Kaola Team
 */
@Data
public class OrderItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 技师ID
     */
    @NotNull(message = "技师ID不能为空")
    private Long masseurId;

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /**
     * 额外时长(分钟)
     */
    private Integer extraDuration;
}
