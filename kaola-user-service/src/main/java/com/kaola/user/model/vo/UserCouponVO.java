package com.kaola.user.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券VO
 *
 * @author Kaola Team
 */
@Data
public class UserCouponVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户优惠券ID
     */
    private Long id;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型
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
     * 状态 (1-未使用 2-已使用 3-已过期)
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusText;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    /**
     * 是否可用
     */
    private Boolean usable;

    /**
     * 领取时间
     */
    private LocalDateTime createTime;
}
