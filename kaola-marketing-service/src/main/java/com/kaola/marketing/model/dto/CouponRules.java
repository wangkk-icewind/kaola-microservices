package com.kaola.marketing.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 优惠券规则DTO
 * 用于解析Coupon.rules字段的JSON内容
 *
 * @author Kaola Team
 */
@Data
public class CouponRules implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 适用门店ID列表（为空表示全部门店适用）
     */
    private List<Long> storeIds;

    /**
     * 适用项目ID列表（为空表示全部项目适用）
     */
    private List<Long> projectIds;

    /**
     * 排除的门店ID列表
     */
    private List<Long> excludeStoreIds;

    /**
     * 排除的项目ID列表
     */
    private List<Long> excludeProjectIds;
}
