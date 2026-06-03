package com.kaola.order.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款申请/审核记录
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_refund")
public class Refund extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("order_id")
    private Long orderId;

    @TableField("order_no")
    private String orderNo;

    @TableField("user_id")
    private Long userId;

    /** 退款金额(元) */
    private BigDecimal amount;

    /** 退款原因 */
    private String reason;

    /** 0-申请中(待审核) 1-已同意已退款 2-已拒绝 */
    private Integer status;

    @TableField("audit_remark")
    private String auditRemark;

    @TableField("transaction_id")
    private String transactionId;

    @TableField("audit_time")
    private LocalDateTime auditTime;
}
