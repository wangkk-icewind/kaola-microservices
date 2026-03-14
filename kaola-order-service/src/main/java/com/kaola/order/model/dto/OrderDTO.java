package com.kaola.order.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 订单DTO
 *
 * @author Kaola Team
 */
@Data
public class OrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 门店ID
     */
    @NotNull(message = "门店ID不能为空")
    private Long storeId;

    /**
     * 订单项列表
     */
    @NotEmpty(message = "订单项不能为空")
    private List<OrderItemDTO> items;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 预约日期 (yyyy-MM-dd)
     */
    private String appointmentDate;

    /**
     * 预约时间 (HH:mm)
     */
    private String appointmentTime;

    /**
     * 订单类型 (SERVICE-服务订单 PRODUCT-商品订单)
     */
    private String orderType;

    /**
     * 收货人姓名 (商品订单使用)
     */
    private String receiverName;

    /**
     * 收货人电话 (商品订单使用)
     */
    private String receiverPhone;

    /**
     * 收货地址 (商品订单使用)
     */
    private String receiverAddress;
}
