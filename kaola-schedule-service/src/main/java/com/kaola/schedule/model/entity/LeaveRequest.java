package com.kaola.schedule.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 请假申请实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_leave_request")
public class LeaveRequest extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 技师ID
     */
    @TableField("masseur_id")
    private Long masseurId;

    /**
     * 请假开始日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 请假结束日期
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 请假原因
     */
    @TableField("reason")
    private String reason;

    /**
     * 状态 (0-待审批 1-已批准 2-已拒绝)
     */
    @TableField("status")
    private Integer status;

    /**
     * 审批备注
     */
    @TableField("remark")
    private String remark;
}
