package com.kaola.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.order.model.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 订单数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据订单号查询订单
     */
    @Select("SELECT * FROM t_order WHERE order_no = #{orderNo} AND deleted = 0")
    Order findByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据用户ID分页查询订单
     */
    @Select("SELECT * FROM t_order WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    IPage<Order> findByUserId(Page<Order> page, @Param("userId") Long userId);

    /**
     * 根据用户ID和状态查询订单
     */
    @Select("SELECT * FROM t_order WHERE user_id = #{userId} AND status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 更新订单状态
     */
    @Update("UPDATE t_order SET status = #{status} WHERE id = #{orderId}")
    int updateStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

    /**
     * 更新支付信息
     */
    @Update("UPDATE t_order SET status = #{status}, pay_method = #{payMethod}, pay_time = #{payTime} WHERE id = #{orderId}")
    int updatePayInfo(@Param("orderId") Long orderId, @Param("status") Integer status,
                      @Param("payMethod") Integer payMethod, @Param("payTime") LocalDateTime payTime);

    /**
     * 查询超时未支付订单
     */
    @Select("SELECT * FROM t_order WHERE status = 1 AND create_time < #{deadline} AND deleted = 0")
    List<Order> findTimeoutOrders(@Param("deadline") LocalDateTime deadline);

    /**
     * 统计用户订单数量
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE user_id = #{userId} AND deleted = 0")
    int countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户消费总额
     */
    @Select("SELECT IFNULL(SUM(pay_amount), 0) FROM t_order WHERE user_id = #{userId} AND status >= 2 AND status != 6 AND deleted = 0")
    BigDecimal sumPayAmountByUserId(@Param("userId") Long userId);

    /**
     * 统计用户已完成订单数（status=4已完成 或 status=5已评价，不含已退款6）
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE user_id = #{userId} AND status IN (4, 5) AND deleted = 0")
    int countCompletedByUserId(@Param("userId") Long userId);

    /**
     * 根据门店ID查询订单
     */
    @Select("SELECT * FROM t_order WHERE store_id = #{storeId} AND deleted = 0 ORDER BY create_time DESC")
    IPage<Order> findByStoreId(Page<Order> page, @Param("storeId") Long storeId);

    /**
     * 统计技师"服务过的客户数"：已完成(4)/已评价(5)订单中去重的下单用户数。
     * 技师通过 t_order_item.masseur_id 关联。
     */
    @Select("SELECT COUNT(DISTINCT o.user_id) FROM t_order o " +
            "INNER JOIN t_order_item oi ON o.id = oi.order_id " +
            "WHERE oi.masseur_id = #{masseurId} " +
            "AND o.status IN (4, 5) " +
            "AND o.deleted = 0 AND oi.deleted = 0")
    int countDistinctCustomersByMasseur(@Param("masseurId") Long masseurId);

    /**
     * 检查技师在指定日期时段是否已有未取消的订单（用于防止重复预约）
     */
    @Select("SELECT COUNT(*) FROM t_order o " +
            "INNER JOIN t_order_item oi ON o.id = oi.order_id " +
            "WHERE oi.masseur_id = #{masseurId} " +
            "AND o.appointment_date = #{date} " +
            "AND o.appointment_time = #{time} " +
            "AND o.status != 0 " +
            "AND o.deleted = 0 AND oi.deleted = 0")
    int countMasseurConflicts(@Param("masseurId") Long masseurId,
                              @Param("date") LocalDate date,
                              @Param("time") LocalTime time);

    /**
     * 检查技师在指定日期时段是否有时间重叠的订单（基于服务时长，防止同时段重复预约）
     * 重叠条件：existing_start < new_end AND existing_end > new_start
     */
    @Select("SELECT COUNT(*) FROM t_order o " +
            "INNER JOIN t_order_item oi ON o.id = oi.order_id " +
            "WHERE oi.masseur_id = #{masseurId} " +
            "AND o.appointment_date = #{date} " +
            "AND o.status != 0 " +
            "AND o.deleted = 0 AND oi.deleted = 0 " +
            "AND o.appointment_time < ADDTIME(#{startTime}, SEC_TO_TIME(#{durationMinutes} * 60)) " +
            "AND ADDTIME(o.appointment_time, SEC_TO_TIME(IFNULL(oi.duration, 60) * 60)) > #{startTime}")
    int countMasseurOverlaps(@Param("masseurId") Long masseurId,
                             @Param("date") LocalDate date,
                             @Param("startTime") LocalTime startTime,
                             @Param("durationMinutes") int durationMinutes);

    /**
     * 查询技师在指定时段有重叠订单时，最晚的空闲开始时间（预约时间+总时长）
     * 返回 "HH:mm" 字符串；无重叠时返回 null
     */
    @Select("SELECT DATE_FORMAT(" +
            "  ADDTIME(o.appointment_time, SEC_TO_TIME((IFNULL(oi.duration, 60) + IFNULL(oi.extra_duration, 0)) * 60)), " +
            "  '%H:%i') " +
            "FROM t_order o " +
            "INNER JOIN t_order_item oi ON o.id = oi.order_id " +
            "WHERE oi.masseur_id = #{masseurId} " +
            "AND o.appointment_date = #{date} " +
            "AND o.status IN (2, 3) " +
            "AND o.deleted = 0 AND oi.deleted = 0 " +
            "AND o.appointment_time < ADDTIME(#{startTime}, SEC_TO_TIME(#{durationMinutes} * 60)) " +
            "AND ADDTIME(o.appointment_time, SEC_TO_TIME(IFNULL(oi.duration, 60) * 60)) > #{startTime} " +
            "ORDER BY ADDTIME(o.appointment_time, SEC_TO_TIME((IFNULL(oi.duration, 60) + IFNULL(oi.extra_duration, 0)) * 60)) DESC " +
            "LIMIT 1")
    String getMasseurFreeAt(@Param("masseurId") Long masseurId,
                            @Param("date") LocalDate date,
                            @Param("startTime") LocalTime startTime,
                            @Param("durationMinutes") int durationMinutes);

    /**
     * 查询指定技师列表在指定日期的所有有效订单时间段
     * 返回: masseurId, startTime("HH:mm"), totalDuration(分钟)
     */
    @Select("<script>" +
            "SELECT oi.masseur_id AS masseurId, " +
            "  DATE_FORMAT(o.appointment_time, '%H:%i') AS startTime, " +
            "  (IFNULL(oi.duration, 60) + IFNULL(oi.extra_duration, 0)) AS totalDuration " +
            "FROM t_order o " +
            "INNER JOIN t_order_item oi ON o.id = oi.order_id " +
            "WHERE oi.masseur_id IN " +
            "  <foreach item='id' collection='masseurIds' open='(' separator=',' close=')'>#{id}</foreach>" +
            "AND o.appointment_date = #{date} " +
            "AND o.status != 0 " +
            "AND o.deleted = 0 AND oi.deleted = 0 " +
            "ORDER BY oi.masseur_id, o.appointment_time" +
            "</script>")
    List<Map<String, Object>> getMasseurDailySlots(@Param("masseurIds") List<Long> masseurIds,
                                                    @Param("date") LocalDate date);
}
