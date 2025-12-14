package com.kaola.marketing.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户优惠券实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_coupon")
public class UserCoupon extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 优惠券ID
     */
    @TableField("coupon_id")
    private Long couponId;

    /**
     * 状态 (0-未使用 1-已使用 2-已过期)
     */
    @TableField("status")
    private Integer status;

    /**
     * 使用时间
     */
    @TableField("use_time")
    private LocalDateTime useTime;

    /**
     * 使用订单ID
     */
    @TableField("order_id")
    private Long orderId;
}
