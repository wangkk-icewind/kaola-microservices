package com.kaola.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 仪表盘数据统计 Mapper
 *
 * @author Kaola Team
 */
@Mapper
public interface DashboardMapper {

    /**
     * 统计今日订单数
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE DATE(create_time) = CURDATE() AND deleted = 0")
    Integer countTodayOrders();

    /**
     * 统计今日收入
     */
    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM t_order " +
            "WHERE DATE(create_time) = CURDATE() AND status >= 2 AND deleted = 0")
    Double getTodayIncome();

    /**
     * 统计今日新增用户
     */
    @Select("SELECT COUNT(*) FROM t_user WHERE DATE(create_time) = CURDATE() AND deleted = 0")
    Integer countTodayUsers();

    /**
     * 统计今日预约数（已支付的订单）
     */
    @Select("SELECT COUNT(*) FROM t_order " +
            "WHERE DATE(appointment_date) = CURDATE() AND status >= 2 AND deleted = 0")
    Integer countTodayAppointments();

    /**
     * 获取最近N天的订单趋势
     * @param days 天数
     */
    @Select("SELECT DATE_FORMAT(order_date, '%m-%d') as date, " +
            "COUNT(*) as count, " +
            "COALESCE(SUM(pay_amount), 0) as amount " +
            "FROM (SELECT DATE(create_time) as order_date, pay_amount FROM t_order " +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "AND status >= 2 AND deleted = 0) AS daily_orders " +
            "GROUP BY order_date " +
            "ORDER BY order_date")
    List<Map<String, Object>> getOrderTrend(@Param("days") int days);

    /**
     * 获取门店排行
     * @param limit 限制数量
     */
    @Select("SELECT s.id as storeId, s.name as storeName, " +
            "COUNT(o.id) as orderCount, " +
            "COALESCE(SUM(o.pay_amount), 0) as income " +
            "FROM t_store s " +
            "LEFT JOIN t_order o ON s.id = o.store_id " +
            "AND o.status >= 2 AND o.deleted = 0 " +
            "WHERE s.deleted = 0 " +
            "GROUP BY s.id, s.name " +
            "ORDER BY orderCount DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getStoreRanking(@Param("limit") int limit);

    /**
     * 获取热门项目排行
     * @param limit 限制数量
     */
    @Select("SELECT p.id as projectId, p.name as projectName, " +
            "COUNT(oi.id) as salesCount " +
            "FROM t_project p " +
            "LEFT JOIN t_order_item oi ON p.id = oi.project_id " +
            "LEFT JOIN t_order o ON oi.order_id = o.id " +
            "AND o.status >= 2 AND o.deleted = 0 " +
            "WHERE p.deleted = 0 " +
            "GROUP BY p.id, p.name " +
            "ORDER BY salesCount DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getHotProjects(@Param("limit") int limit);
}
