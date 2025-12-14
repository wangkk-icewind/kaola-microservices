package com.kaola.payment.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付信息VO
 *
 * @author Kaola Team
 */
@Data
public class PaymentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付ID
     */
    private Long id;

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付方式
     */
    private Integer payMethod;

    /**
     * 支付方式描述
     */
    private String payMethodText;

    /**
     * 支付状态
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusText;

    /**
     * 第三方交易号
     */
    private String transactionId;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 微信支付参数（调起支付用）
     */
    private Object wxPayParams;
}
