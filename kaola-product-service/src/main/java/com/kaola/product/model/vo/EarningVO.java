package com.kaola.product.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收益记录VO
 *
 * @author Kaola Team
 */
@Data
public class EarningVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收益ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 收益类型
     */
    private Integer type;

    /**
     * 类型描述
     */
    private String typeText;

    /**
     * 收益金额
     */
    private BigDecimal amount;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 收益时间
     */
    private LocalDateTime createTime;
}
