package com.kaola.product.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 收益统计VO
 *
 * @author Kaola Team
 */
@Data
public class EarningStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总收益
     */
    private BigDecimal totalEarning;

    /**
     * 本月收益
     */
    private BigDecimal monthEarning;

    /**
     * 本周收益
     */
    private BigDecimal weekEarning;

    /**
     * 今日收益
     */
    private BigDecimal todayEarning;

    /**
     * 可提现金额
     */
    private BigDecimal withdrawableAmount;

    /**
     * 提现中金额
     */
    private BigDecimal withdrawingAmount;

    /**
     * 已提现总额
     */
    private BigDecimal withdrawnAmount;

    /**
     * 可用余额
     */
    private BigDecimal availableBalance;

    /**
     * 待结算余额
     */
    private BigDecimal pendingBalance;

    /**
     * 今日订单数
     */
    private Integer todayOrderCount;

    /**
     * 本月订单数
     */
    private Integer monthOrderCount;

    /**
     * 总订单数
     */
    private Integer totalOrderCount;
}
