package com.kaola.common.model.enums;

import lombok.Getter;

/**
 * 收益类型枚举
 *
 * @author Kaola Team
 */
@Getter
public enum EarningType {

    SERVICE(1, "服务收益"),
    TIP(2, "小费"),
    BONUS(3, "奖金"),
    COMMISSION(4, "提成"),
    OTHER(5, "其他");

    private final Integer code;
    private final String description;

    EarningType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static EarningType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (EarningType type : values()) {
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
        EarningType type = getByCode(code);
        return type != null ? type.getDescription() : "";
    }
}
