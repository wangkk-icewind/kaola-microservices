package com.kaola.product.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.product.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 时段类型价格系数配置实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_time_slot_pricing")
public class TimeSlotPricing extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 时段类型 (1-普通时段 2-高峰时段 3-夜间时段)
     */
    @TableField("slot_type")
    private Integer slotType;

    /**
     * 时段名称
     */
    @TableField("slot_name")
    private String slotName;

    /**
     * 时间范围 (JSON数组格式，如 [{"start":"10:00","end":"18:00"}])
     */
    @TableField("time_ranges")
    private String timeRanges;

    /**
     * 价格系数
     */
    @TableField("multiplier")
    private BigDecimal multiplier;

    /**
     * 适用星期 (JSON数组格式，如 [1,2,3,4,5] 表示周一到周五，空表示全部)
     */
    @TableField("day_of_week")
    private String dayOfWeek;

    /**
     * 描述说明
     */
    @TableField("description")
    private String description;

    /**
     * 状态 (0-禁用 1-启用)
     */
    @TableField("status")
    private Integer status;
}
