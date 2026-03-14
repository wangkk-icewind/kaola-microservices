package com.kaola.marketing.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 验证优惠券响应
 * 返回优惠券验证结果及优惠金额
 *
 * @author Kaola Team
 */
@Data
public class ValidateCouponResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否可用
     */
    private Boolean valid;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 最终金额（订单金额 - 优惠金额）
     */
    private BigDecimal finalAmount;

    /**
     * 不可用原因（valid=false时）
     */
    private String reason;
}
