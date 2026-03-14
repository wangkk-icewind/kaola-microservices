package com.kaola.product.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 定价配置VO - 用于小程序端获取定价配置
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

    /**
     * 技师等级定价项
     */
    @Data
    public static class MasseurLevelPricingItem implements Serializable {
        private Integer level;
        private String levelName;
        private BigDecimal multiplier;
    }

    /**
     * 时段定价项
     */
    @Data
    public static class TimeSlotPricingItem implements Serializable {
        private Integer slotType;
        private String slotName;
        private BigDecimal multiplier;
    }

    /**
     * 门店定价项
     */
    @Data
    public static class StorePricingItem implements Serializable {
        private Long storeId;
        private String storeName;
        private BigDecimal multiplier;
    }
}
