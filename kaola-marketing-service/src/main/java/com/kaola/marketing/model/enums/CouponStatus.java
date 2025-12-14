package com.kaola.marketing.model.enums;

import lombok.Getter;

/**
 * 优惠券状态枚举
 *
 * @author Kaola Team
 */
@Getter
public enum CouponStatus {

    UNUSED(1, "未使用"),
    USED(2, "已使用"),
    EXPIRED(3, "已过期");

    private final Integer code;
    private final String description;

    CouponStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static CouponStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CouponStatus status : values()) {
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
        CouponStatus status = getByCode(code);
        return status != null ? status.getDescription() : "";
    }
}
