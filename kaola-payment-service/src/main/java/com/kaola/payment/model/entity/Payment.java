package com.kaola.payment.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_payment")
public class Payment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 内部支付单号（KP + 时间戳 + 随机4位） */
    @TableField("payment_no")
    private String paymentNo;

    /** 订单ID */
    @TableField("order_id")
    private Long orderId;

    /** 订单编号（冗余，方便查询） */
    @TableField("order_no")
    private String orderNo;

    /** 用户ID */
    @TableField("user_id")
    private Long userId;

    /** 用户微信 openid（JSAPI 必需） */
    @TableField("openid")
    private String openid;

    /** 支付方式 (1-微信支付 2-支付宝 3-余额支付) */
    @TableField("pay_method")
    private Integer payMethod;

    /** 微信预支付 prepay_id */
    @TableField("prepay_id")
    private String prepayId;

    /** 第三方交易流水号（微信 transaction_id） */
    @TableField("transaction_id")
    private String transactionId;

    /** 支付金额（单位：元） */
    @TableField("amount")
    private BigDecimal amount;

    /** 支付状态 (0-待支付 1-支付成功 2-支付失败 3-已退款 4-已关闭) */
    @TableField("status")
    private Integer status;

    /** 支付时间 */
    @TableField("pay_time")
    private LocalDateTime payTime;
}
