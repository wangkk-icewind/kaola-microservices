package com.kaola.product.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 排班VO
 *
 * @author Kaola Team
 */
@Data
public class ScheduleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 星期几
     */
    private String weekDay;

    /**
     * 是否今天
     */
    private Boolean isToday;

    /**
     * 可用时段数
     */
    private Integer availableCount;

    /**
     * 时段列表
     */
    private List<TimeSlotVO> timeSlots;
}
