package com.kaola.order.model.enums;

/**
 * 订单类型枚举
 *
 * @author Kaola Team
 */
public enum OrderType {
    /**
     * 服务订单 (按摩/推拿服务)
     */
    SERVICE("SERVICE", "服务订单"),

    /**
     * 商品订单 (礼卡/实物商品)
     */
    PRODUCT("PRODUCT", "商品订单");

    private final String code;
    private final String desc;

    OrderType(String code, String desc) {
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
