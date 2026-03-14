package com.kaola.marketing.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 可用优惠券视图对象
 * 用于返回订单可用的优惠券列表（含优惠金额）
 *
 * @author Kaola Team
 */
@Data
public class AvailableCouponVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户优惠券ID
     */
    private Long userCouponId;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型 (1-满减券 2-折扣券 3-代金券)
     */
    private Integer type;

    /**
     * 优惠券面值
     */
    private BigDecimal value;

    /**
     * 最低消费金额
     */
    private BigDecimal minAmount;

    /**
     * 优惠金额（计算后的实际优惠）
     */
    private BigDecimal discountAmount;

    /**
     * 是否可用
     */
    private Boolean canUse;

    /**
     * 不可用原因
     */
    private String reason;
}
