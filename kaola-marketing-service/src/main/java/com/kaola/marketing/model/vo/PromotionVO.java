package com.kaola.marketing.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 促销活动VO
 *
 * @author Kaola Team
 */
@Data
public class PromotionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动图片
     */
    private String image;

    /**
     * 活动类型
     */
    private Integer type;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 跳转链接
     */
    private String linkUrl;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 是否进行中
     */
    private Boolean isActive;

    /**
     * 剩余时间描述
     */
    private String remainingTime;

    /**
     * 折扣规则 (JSON字符串，含 discount/reduce/timeSlotStart/timeSlotEnd 等字段)
     */
    private String rules;

    /**
     * 活动类型原始折扣值 (type=2时为折扣系数，如0.95)
     */
    private java.math.BigDecimal discount;

    /**
     * 关联门店ID（null表示全局促销）
     */
    private Long storeId;
}
