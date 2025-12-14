package com.kaola.common.model.enums;

import lombok.Getter;

/**
 * 技师等级枚举
 *
 * @author Kaola Team
 */
@Getter
public enum MasseurLevel {

    JUNIOR(1, "初级技师"),
    INTERMEDIATE(2, "中级技师"),
    SENIOR(3, "高级技师"),
    EXPERT(4, "专家技师"),
    MASTER(5, "大师技师");

    private final Integer code;
    private final String description;

    MasseurLevel(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static MasseurLevel getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (MasseurLevel level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return null;
    }

    /**
     * 根据code获取描述
     */
    public static String getDescriptionByCode(Integer code) {
        MasseurLevel level = getByCode(code);
        return level != null ? level.getDescription() : "";
    }
}
