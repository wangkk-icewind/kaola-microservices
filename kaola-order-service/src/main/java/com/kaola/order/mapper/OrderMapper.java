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
import java.time.LocalDateTime;
import java.util.List;

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
    @Select("SELECT IFNULL(SUM(pay_amount), 0) FROM t_order WHERE user_id = #{userId} AND status >= 2 AND deleted = 0")
    BigDecimal sumPayAmountByUserId(@Param("userId") Long userId);

    /**
     * 根据门店ID查询订单
     */
    @Select("SELECT * FROM t_order WHERE store_id = #{storeId} AND deleted = 0 ORDER BY create_time DESC")
    IPage<Order> findByStoreId(Page<Order> page, @Param("storeId") Long storeId);
}
