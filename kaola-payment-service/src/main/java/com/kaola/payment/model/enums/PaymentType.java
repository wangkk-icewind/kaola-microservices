package com.kaola.payment.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Payment type enumeration
 */
@Getter
@AllArgsConstructor
public enum PaymentType {

    WECHAT(1, "微信支付"),
    ALIPAY(2, "支付宝"),
    BALANCE(3, "余额支付");

    /**
     * Payment type code
     */
    private final Integer code;

    /**
     * Payment type description
     */
    private final String description;

    /**
     * Get PaymentType by code
     *
     * @param code the payment type code
     * @return PaymentType or null if not found
     */
    public static PaymentType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PaymentType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Check if the code is valid
     *
     * @param code the payment type code
     * @return true if valid, false otherwise
     */
    public static boolean isValid(Integer code) {
        return getByCode(code) != null;
    }
}
