package com.kaola.schedule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.schedule.model.entity.LeaveRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 请假申请数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface LeaveRequestMapper extends BaseMapper<LeaveRequest> {

    /**
     * 根据技师ID查询请假记录
     */
    @Select("SELECT * FROM t_leave_request WHERE masseur_id = #{masseurId} AND deleted = 0 ORDER BY create_time DESC")
    List<LeaveRequest> findByMasseurId(@Param("masseurId") Long masseurId);

    /**
     * 根据技师ID和状态查询请假记录
     */
    @Select("SELECT * FROM t_leave_request WHERE masseur_id = #{masseurId} AND status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<LeaveRequest> findByMasseurIdAndStatus(@Param("masseurId") Long masseurId, @Param("status") Integer status);

    /**
     * 查询技师在指定日期范围内的请假记录
     */
    @Select("SELECT * FROM t_leave_request WHERE masseur_id = #{masseurId} " +
            "AND ((start_date <= #{endDate} AND end_date >= #{startDate})) " +
            "AND status = 1 AND deleted = 0")
    List<LeaveRequest> findApprovedLeaveInDateRange(
            @Param("masseurId") Long masseurId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 查询待审批的请假申请
     */
    @Select("SELECT * FROM t_leave_request WHERE status = 0 AND deleted = 0 ORDER BY create_time DESC")
    List<LeaveRequest> findPendingLeaves();
}
