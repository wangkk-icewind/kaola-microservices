package com.kaola.order.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 订单实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order")
public class Order extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 订单编号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 门店ID
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 订单总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    @TableField("pay_amount")
    private BigDecimal payAmount;

    /**
     * 订单状态 (0-已取消 1-待支付 2-已支付 3-服务中 4-已完成 5-已评价)
     */
    @TableField("status")
    private Integer status;

    /**
     * 支付方式 (1-微信支付 2-支付宝 3-余额支付)
     */
    @TableField("pay_method")
    private Integer payMethod;

    /**
     * 支付时间
     */
    @TableField("pay_time")
    private LocalDateTime payTime;

    /**
     * 预约日期
     */
    @TableField("appointment_date")
    private LocalDate appointmentDate;

    /**
     * 预约时间
     */
    @TableField("appointment_time")
    private LocalTime appointmentTime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
