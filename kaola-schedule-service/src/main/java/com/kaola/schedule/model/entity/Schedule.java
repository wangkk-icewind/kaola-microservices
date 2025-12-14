package com.kaola.schedule.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 技师排班实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_schedule")
public class Schedule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 技师ID
     */
    @TableField("masseur_id")
    private Long masseurId;

    /**
     * 门店ID
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 排班日期
     */
    @TableField("date")
    private LocalDate date;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalTime endTime;

    /**
     * 状态 (0-取消 1-正常 2-已完成)
     */
    @TableField("status")
    private Integer status;
}
