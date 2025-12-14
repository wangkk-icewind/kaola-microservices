package com.kaola.masseur.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 技师收益实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_masseur_earning")
public class MasseurEarning extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 技师ID
     */
    @TableField("masseur_id")
    private Long masseurId;

    /**
     * 订单ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 收益金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 收益类型 (1-服务提成 2-加钟提成 3-好评奖励 4-其他奖励)
     */
    @TableField("type")
    private Integer type;

    /**
     * 状态 (0-待结算 1-已结算 2-已提现)
     */
    @TableField("status")
    private Integer status;

    /**
     * 结算时间
     */
    @TableField("settle_time")
    private LocalDateTime settleTime;
}
