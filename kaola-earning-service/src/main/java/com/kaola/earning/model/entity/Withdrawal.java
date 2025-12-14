package com.kaola.earning.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_withdrawal")
public class Withdrawal extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 技师ID
     */
    @TableField("masseur_id")
    private Long masseurId;

    /**
     * 提现金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 银行账户信息 (JSON格式，包含银行名称、卡号、户名)
     */
    @TableField("bank_account")
    private String bankAccount;

    /**
     * 状态 (0-待审核 1-审核通过 2-处理中 3-已完成 4-已拒绝)
     */
    @TableField("status")
    private Integer status;

    /**
     * 申请时间
     */
    @TableField("apply_time")
    private LocalDateTime applyTime;

    /**
     * 处理时间
     */
    @TableField("process_time")
    private LocalDateTime processTime;
}
