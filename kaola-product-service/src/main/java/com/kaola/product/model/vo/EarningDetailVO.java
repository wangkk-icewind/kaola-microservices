package com.kaola.product.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收益详情VO - 包含技师信息和订单信息
 */
@Data
public class EarningDetailVO {

    /**
     * 收益记录ID
     */
    private Long id;

    /**
     * 技师ID
     */
    private Long masseurId;

    /**
     * 技师姓名
     */
    private String masseurName;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 收益金额
     */
    private BigDecimal amount;

    /**
     * 收益类型：1-服务收益 2-提成 3-奖励 4-其他
     */
    private Integer type;

    /**
     * 收益类型名称
     */
    private String typeName;

    /**
     * 状态：0-待结算 1-已结算 2-已取消
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 结算时间
     */
    private LocalDateTime settleTime;
}
