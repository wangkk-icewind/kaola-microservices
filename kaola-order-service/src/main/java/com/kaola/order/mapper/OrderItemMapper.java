package com.kaola.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.order.model.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单项数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 根据订单ID查询订单项
     */
    @Select("SELECT * FROM t_order_item WHERE order_id = #{orderId} AND deleted = 0")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据订单ID和技师ID查询订单项
     */
    @Select("SELECT * FROM t_order_item WHERE order_id = #{orderId} AND masseur_id = #{masseurId} AND deleted = 0")
    List<OrderItem> findByOrderIdAndMasseurId(@Param("orderId") Long orderId, @Param("masseurId") Long masseurId);

    /**
     * 根据技师ID查询订单项
     */
    @Select("SELECT * FROM t_order_item WHERE masseur_id = #{masseurId} AND deleted = 0 ORDER BY create_time DESC")
    List<OrderItem> findByMasseurId(@Param("masseurId") Long masseurId);

    /**
     * 批量插入订单项
     */
    int batchInsert(@Param("items") List<OrderItem> items);

    /**
     * 根据订单ID删除订单项
     */
    @Select("DELETE FROM t_order_item WHERE order_id = #{orderId}")
    int deleteByOrderId(@Param("orderId") Long orderId);
}
