package com.kaola.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.order.model.entity.Refund;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 退款申请数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface RefundMapper extends BaseMapper<Refund> {

    /** 查询订单进行中的退款申请(status=0)，无则 null */
    @Select("SELECT * FROM t_refund WHERE order_id = #{orderId} AND status = 0 AND deleted = 0 LIMIT 1")
    Refund findPendingByOrderId(@Param("orderId") Long orderId);
}
