package com.kaola.marketing.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 定价配置响应对象
 *
 * @author Kaola Team
 */
@Data
public class PricingConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 技师等级定价列表
     */
    private List<MasseurLevelPricingItem> masseurLevelPricing;

    /**
     * 时段定价列表
     */
    private List<TimeSlotPricingItem> timeSlotPricing;

    /**
     * 门店定价列表
     */
    private List<StorePricingItem> storePricing;

    @Data
    public static class MasseurLevelPricingItem implements Serializable {
        /**
         * 技师等级 (1-初级 2-中级 3-高级 4-特级)
         */
        private Integer level;

        /**
         * 等级名称
         */
        private String levelName;

        /**
         * 价格倍率
         */
        private BigDecimal multiplier;
    }

    @Data
    public static class TimeSlotPricingItem implements Serializable {
        /**
         * 时段类型 (1-正常时段 2-高峰时段 3-深夜时段)
         */
        private Integer slotType;

        /**
         * 时段名称
         */
        private String slotName;

        /**
         * 时间范围（JSON数组）
         */
        private String timeRanges;

        /**
         * 适用星期（JSON数组）
         */
        private String dayOfWeek;

        /**
         * 价格倍率
         */
        private BigDecimal multiplier;
    }

    @Data
    public static class StorePricingItem implements Serializable {
        /**
         * 门店ID
         */
        private Long storeId;

        /**
         * 门店名称
         */
        private String storeName;

        /**
         * 价格倍率
         */
        private BigDecimal multiplier;
    }
}
