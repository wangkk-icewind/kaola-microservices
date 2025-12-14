package com.kaola.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.payment.model.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 支付记录数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {

    /**
     * 根据订单ID查询支付记录
     */
    @Select("SELECT * FROM t_payment WHERE order_id = #{orderId} AND deleted = 0")
    Payment findByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据支付单号查询支付记录
     */
    @Select("SELECT * FROM t_payment WHERE payment_no = #{paymentNo} AND deleted = 0")
    Payment findByPaymentNo(@Param("paymentNo") String paymentNo);

    /**
     * 根据第三方交易号查询支付记录
     */
    @Select("SELECT * FROM t_payment WHERE transaction_id = #{transactionId} AND deleted = 0")
    Payment findByTransactionId(@Param("transactionId") String transactionId);

    /**
     * 更新支付状态
     */
    @Update("UPDATE t_payment SET status = #{status}, transaction_id = #{transactionId}, pay_time = NOW() WHERE id = #{paymentId}")
    int updatePayStatus(@Param("paymentId") Long paymentId, @Param("status") Integer status, @Param("transactionId") String transactionId);

    /**
     * 根据用户ID查询支付记录
     */
    @Select("SELECT * FROM t_payment WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<Payment> findByUserId(@Param("userId") Long userId);

    /**
     * 查询待支付的支付记录
     */
    @Select("SELECT * FROM t_payment WHERE status = 0 AND deleted = 0")
    List<Payment> findPendingPayments();
}
