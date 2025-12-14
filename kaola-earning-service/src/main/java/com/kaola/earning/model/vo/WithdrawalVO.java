package com.kaola.earning.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录VO
 *
 * @author Kaola Team
 */
@Data
public class WithdrawalVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 提现ID
     */
    private Long id;

    /**
     * 提现单号
     */
    private String withdrawalNo;

    /**
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 实际到账
     */
    private BigDecimal actualAmount;

    /**
     * 提现方式
     */
    private Integer method;

    /**
     * 提现方式描述
     */
    private String methodText;

    /**
     * 收款账号
     */
    private String account;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusText;

    /**
     * 备注/原因
     */
    private String remark;

    /**
     * 申请时间
     */
    private LocalDateTime createTime;

    /**
     * 处理时间
     */
    private LocalDateTime processTime;
}
