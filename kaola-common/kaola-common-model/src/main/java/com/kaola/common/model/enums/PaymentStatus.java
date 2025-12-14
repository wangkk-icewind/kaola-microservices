package com.kaola.common.model.enums;

import lombok.Getter;

/**
 * 支付状态枚举
 *
 * @author Kaola Team
 */
@Getter
public enum PaymentStatus {

    PENDING(0, "待支付"),
    SUCCESS(1, "支付成功"),
    FAILED(2, "支付失败"),
    REFUNDED(3, "已退款"),
    CLOSED(4, "已关闭");

    private final Integer code;
    private final String description;

    PaymentStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static PaymentStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PaymentStatus status : values()) {
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
        PaymentStatus status = getByCode(code);
        return status != null ? status.getDescription() : "";
    }
}
