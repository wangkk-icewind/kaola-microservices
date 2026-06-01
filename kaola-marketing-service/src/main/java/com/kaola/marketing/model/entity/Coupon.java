package com.kaola.marketing.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_coupon")
public class Coupon extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 优惠券名称
     */
    @TableField("name")
    private String name;

    /**
     * 优惠券类型 (1-满减券 2-折扣券 3-代金券)
     */
    @TableField("type")
    private Integer type;

    /**
     * 优惠值 (满减券和代金券为金额，折扣券为折扣比例如0.8表示8折)
     */
    @TableField("value")
    private BigDecimal value;

    /**
     * 最低消费金额
     */
    @TableField("min_amount")
    private BigDecimal minAmount;

    /**
     * 有效期开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 有效期结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 发放总量
     */
    @TableField("total_count")
    private Integer totalCount;

    /**
     * 已使用数量
     */
    @TableField("used_count")
    private Integer usedCount;

    /**
     * 使用规则 (JSON格式，如适用门店、适用项目等)
     */
    @TableField("rules")
    private String rules;

    /**
     * 状态 (0-禁用 1-启用)
     */
    @TableField("status")
    private Integer status;

    /**
     * 适用客群 (0-全部 1-新客 2-老客)
     */
    @TableField("customer_type")
    private Integer customerType;

    /**
     * 完成奖励券 (0-否 1-是)：启用后用户每完成一笔订单自动发放一张（每人限领一次）
     */
    @TableField("is_completion_reward")
    private Integer isCompletionReward;
}
