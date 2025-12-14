package com.kaola.common.model.enums;

import lombok.Getter;

/**
 * 提现状态枚举
 *
 * @author Kaola Team
 */
@Getter
public enum WithdrawalStatus {

    PENDING(0, "待审核"),
    PROCESSING(1, "处理中"),
    SUCCESS(2, "提现成功"),
    FAILED(3, "提现失败"),
    REJECTED(4, "已驳回");

    private final Integer code;
    private final String description;

    WithdrawalStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static WithdrawalStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (WithdrawalStatus status : values()) {
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
        WithdrawalStatus status = getByCode(code);
        return status != null ? status.getDescription() : "";
    }
}
