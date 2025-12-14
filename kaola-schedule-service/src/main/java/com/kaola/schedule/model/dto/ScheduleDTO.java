package com.kaola.schedule.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 排班DTO
 *
 * @author Kaola Team
 */
@Data
public class ScheduleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 门店ID
     */
    @NotNull(message = "门店ID不能为空")
    private Long storeId;

    /**
     * 日期 (yyyy-MM-dd)
     */
    @NotBlank(message = "日期不能为空")
    private String date;

    /**
     * 开始时间 (HH:mm)
     */
    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    /**
     * 结束时间 (HH:mm)
     */
    @NotBlank(message = "结束时间不能为空")
    private String endTime;
}
