package com.kaola.common.model.enums;

import lombok.Getter;

/**
 * 投诉状态枚举
 *
 * @author Kaola Team
 */
@Getter
public enum ComplaintStatus {

    PENDING(0, "待处理"),
    PROCESSING(1, "处理中"),
    RESOLVED(2, "已解决"),
    REJECTED(3, "已驳回"),
    CLOSED(4, "已关闭");

    private final Integer code;
    private final String description;

    ComplaintStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static ComplaintStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ComplaintStatus status : values()) {
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
        ComplaintStatus status = getByCode(code);
        return status != null ? status.getDescription() : "";
    }
}
