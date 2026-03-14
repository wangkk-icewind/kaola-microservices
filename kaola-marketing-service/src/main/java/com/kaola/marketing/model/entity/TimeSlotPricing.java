package com.kaola.marketing.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 时段定价实体
 *
 * @author Kaola Team
 */
@Data
@TableName("t_time_slot_pricing")
public class TimeSlotPricing implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 描述说明
     */
    private String description;

    /**
     * 价格倍率
     */
    private BigDecimal multiplier;

    /**
     * 状态 (1-启用 0-禁用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
