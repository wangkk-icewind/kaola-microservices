package com.kaola.marketing.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 验证优惠券请求
 * 用于验证优惠券是否可用于当前订单
 *
 * @author Kaola Team
 */
@Data
public class ValidateCouponRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户优惠券ID
     */
    private Long userCouponId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 项目ID列表
     */
    private List<Long> projectIds;
}
