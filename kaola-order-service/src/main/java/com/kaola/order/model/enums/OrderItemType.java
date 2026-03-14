package com.kaola.order.model.enums;

/**
 * 订单项类型枚举
 *
 * @author Kaola Team
 */
public enum OrderItemType {
    /**
     * 服务项 (按摩/推拿服务)
     */
    SERVICE("SERVICE", "服务项"),

    /**
     * 商品项 (礼卡/实物商品)
     */
    PRODUCT("PRODUCT", "商品项");

    private final String code;
    private final String desc;

    OrderItemType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
