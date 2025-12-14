package com.kaola.common.model.enums;

import lombok.Getter;

/**
 * 支付方式枚举
 *
 * @author Kaola Team
 */
@Getter
public enum PaymentMethod {

    WECHAT(1, "微信支付"),
    ALIPAY(2, "支付宝"),
    BALANCE(3, "余额支付");

    private final Integer code;
    private final String description;

    PaymentMethod(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static PaymentMethod getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PaymentMethod method : values()) {
            if (method.getCode().equals(code)) {
                return method;
            }
        }
        return null;
    }

    /**
     * 根据code获取描述
     */
    public static String getDescriptionByCode(Integer code) {
        PaymentMethod method = getByCode(code);
        return method != null ? method.getDescription() : "";
    }
}
