package com.kaola.product.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 订单详情VO
 *
 * @author Kaola Team
 */
@Data
public class OrderDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 订单状态描述
     */
    private String statusText;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 预约日期
     */
    private LocalDate appointmentDate;

    /**
     * 预约时间
     */
    private LocalTime appointmentTime;

    /**
     * 支付方式
     */
    private Integer payMethod;

    /**
     * 支付方式描述
     */
    private String payMethodText;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 门店信息
     */
    private StoreVO store;

    /**
     * 订单项列表
     */
    private List<OrderItemVO> items;

    /**
     * 优惠券信息
     */
    private UserCouponVO coupon;

    /**
     * 是否已评价
     */
    private Boolean reviewed;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 支付截止时间
     */
    private LocalDateTime payDeadline;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 取消原因
     */
    private String cancelReason;
}
