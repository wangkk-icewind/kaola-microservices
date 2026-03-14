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
     * 订单项类型 (SERVICE-服务项 PRODUCT-商品项)
     */
    @NotNull(message = "订单项类型不能为空")
    private String itemType;

    /**
     * 技师ID (服务项使用)
     */
    private Long masseurId;

    /**
     * 项目ID (服务项使用)
     */
    private Long projectId;

    /**
     * 商品ID (商品项使用)
     */
    private Long productId;

    /**
     * 数量 (商品项使用，默认1)
     */
    private Integer quantity;

    /**
     * 额外时长(分钟) (服务项使用)
     */
    private Integer extraDuration;
}
