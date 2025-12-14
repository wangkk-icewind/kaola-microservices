package com.kaola.complaint.model.enums;

import lombok.Getter;

/**
 * 投诉类型枚举
 *
 * @author Kaola Team
 */
@Getter
public enum ComplaintType {

    SERVICE_QUALITY(1, "服务质量"),
    SERVICE_ATTITUDE(2, "服务态度"),
    ENVIRONMENT(3, "环境问题"),
    PRICE(4, "价格问题"),
    APPOINTMENT(5, "预约问题"),
    OTHER(6, "其他问题");

    private final Integer code;
    private final String description;

    ComplaintType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static ComplaintType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ComplaintType type : values()) {
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
        ComplaintType type = getByCode(code);
        return type != null ? type.getDescription() : "";
    }
}
