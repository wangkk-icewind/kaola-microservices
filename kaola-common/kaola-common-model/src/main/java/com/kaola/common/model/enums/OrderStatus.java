package com.kaola.common.model.enums;

import lombok.Getter;

/**
 * 订单状态枚举
 *
 * @author Kaola Team
 */
@Getter
public enum OrderStatus {

    CANCELLED(0, "已取消"),
    PENDING_PAYMENT(1, "待支付"),
    PAID(2, "已支付"),
    IN_SERVICE(3, "服务中"),
    COMPLETED(4, "已完成"),
    REVIEWED(5, "已评价"),
    REFUNDED(6, "已退款");

    private final Integer code;
    private final String description;

    OrderStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static OrderStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrderStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据code获取描述
     */
    public static String getDescriptionByCode(Integer code) {
        OrderStatus status = getByCode(code);
        return status != null ? status.getDescription() : "";
    }
}
