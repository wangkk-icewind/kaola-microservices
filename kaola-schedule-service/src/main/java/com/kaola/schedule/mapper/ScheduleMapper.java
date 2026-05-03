package com.kaola.schedule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.schedule.model.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 排班数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

    /**
     * 根据技师ID和日期查询排班
     */
    @Select("SELECT * FROM t_schedule WHERE masseur_id = #{masseurId} AND date = #{date} AND deleted = 0")
    List<Schedule> findByMasseurIdAndDate(@Param("masseurId") Long masseurId, @Param("date") LocalDate date);

    /**
     * 查询技师某天可用时段
     */
    @Select("SELECT * FROM t_schedule WHERE masseur_id = #{masseurId} AND date = #{date} AND status = 1 AND deleted = 0")
    List<Schedule> findAvailableSlots(@Param("masseurId") Long masseurId, @Param("date") LocalDate date);

    /**
     * 查询指定时段是否可用
     */
    @Select("SELECT * FROM t_schedule WHERE masseur_id = #{masseurId} AND date = #{date} " +
            "AND start_time = #{startTime} AND status = 1 AND deleted = 0")
    Schedule findAvailableSlot(@Param("masseurId") Long masseurId, @Param("date") LocalDate date, @Param("startTime") LocalTime startTime);

    /**
     * 锁定时段
     */
    @Update("UPDATE t_schedule SET status = 2, order_id = #{orderId} WHERE id = #{scheduleId} AND status = 1")
    int lockSlot(@Param("scheduleId") Long scheduleId, @Param("orderId") Long orderId);

    /**
     * 释放时段
     */
    @Update("UPDATE t_schedule SET status = 1, order_id = NULL WHERE id = #{scheduleId}")
    int releaseSlot(@Param("scheduleId") Long scheduleId);

    /**
     * 根据订单ID查询排班
     */
    @Select("SELECT * FROM t_schedule WHERE order_id = #{orderId} AND deleted = 0")
    List<Schedule> findByOrderId(@Param("orderId") Long orderId);

    /**
     * 批量创建排班
     */
    int batchInsert(@Param("schedules") List<Schedule> schedules);
}
