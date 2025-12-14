package com.kaola.payment.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_payment")
public class Payment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 支付方式 (1-微信支付 2-支付宝 3-余额支付)
     */
    @TableField("pay_method")
    private Integer payMethod;

    /**
     * 第三方交易流水号
     */
    @TableField("transaction_id")
    private String transactionId;

    /**
     * 支付金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 支付状态 (0-待支付 1-支付成功 2-支付失败 3-已退款)
     */
    @TableField("status")
    private Integer status;

    /**
     * 支付时间
     */
    @TableField("pay_time")
    private LocalDateTime payTime;
}
