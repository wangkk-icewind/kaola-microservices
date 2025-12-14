package com.kaola.marketing.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券VO
 *
 * @author Kaola Team
 */
@Data
public class CouponVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 优惠券ID
     */
    private Long id;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型 (1-满减券 2-折扣券 3-代金券)
     */
    private Integer type;

    /**
     * 类型描述
     */
    private String typeText;

    /**
     * 优惠金额/折扣
     */
    private BigDecimal value;

    /**
     * 最低消费金额
     */
    private BigDecimal minAmount;

    /**
     * 优惠描述
     */
    private String description;

    /**
     * 使用规则
     */
    private String rule;

    /**
     * 有效期开始
     */
    private LocalDateTime startTime;

    /**
     * 有效期结束
     */
    private LocalDateTime endTime;

    /**
     * 剩余数量
     */
    private Integer remainCount;

    /**
     * 是否可领取
     */
    private Boolean canReceive;

    /**
     * 每人限领数量
     */
    private Integer limitPerUser;
}
