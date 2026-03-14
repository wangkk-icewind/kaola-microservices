package com.kaola.product.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * 时段VO
 *
 * @author Kaola Team
 */
@Data
public class TimeSlotVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排班ID
     */
    private Long scheduleId;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 时间显示文本
     */
    private String timeText;

    /**
     * 状态 (1-可预约 2-已预约 3-已过期)
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusText;

    /**
     * 是否可选
     */
    private Boolean selectable;
}
