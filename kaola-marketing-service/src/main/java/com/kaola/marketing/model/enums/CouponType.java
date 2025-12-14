package com.kaola.marketing.model.enums;

import lombok.Getter;

/**
 * 优惠券类型枚举
 *
 * @author Kaola Team
 */
@Getter
public enum CouponType {

    FULL_REDUCTION(1, "满减券"),
    DISCOUNT(2, "折扣券"),
    VOUCHER(3, "代金券");

    private final Integer code;
    private final String description;

    CouponType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static CouponType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CouponType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据code获取描述
     */
    public static String getDescriptionByCode(Integer code) {
        CouponType type = getByCode(code);
        return type != null ? type.getDescription() : "";
    }
}
