package com.kaola.payment.service;

import com.kaola.payment.model.vo.PaymentVO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付服务接口
 *
 * @author Kaola Team
 */
public interface PaymentService {

    /**
     * 创建微信支付
     *
     * @param orderId 订单ID
     * @return 支付信息
     */
    PaymentVO createWechatPayment(Long orderId);

    /**
     * 创建支付宝支付
     *
     * @param orderId 订单ID
     * @return 支付信息
     */
    PaymentVO createAlipayPayment(Long orderId);

    /**
     * 处理微信支付回调
     *
     * @param xml 回调XML数据
     * @return 处理结果
     */
    String handleWechatNotify(String xml);

    /**
     * 处理支付宝回调
     *
     * @param params 回调参数
     * @return 处理结果
     */
    String handleAlipayNotify(Map<String, String> params);

    /**
     * 退款
     *
     * @param orderId 订单ID
     * @param amount  退款金额
     * @return 是否成功
     */
    boolean refund(Long orderId, BigDecimal amount);
}
