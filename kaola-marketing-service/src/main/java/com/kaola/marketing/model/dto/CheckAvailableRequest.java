package com.kaola.marketing.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 检查可用优惠券请求
 * 用于获取订单可用的优惠券列表
 *
 * @author Kaola Team
 */
@Data
public class CheckAvailableRequest implements Serializable {

    private static final long serialVersionUID = 1L;

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

    /**
     * 是否新客 (用于过滤 customer_type=1 的新客专属券)
     */
    private Boolean isNewCustomer;
}
