package com.kaola.payment.service;

import com.kaola.payment.model.vo.PaymentVO;

import java.math.BigDecimal;

public interface PaymentService {

    /**
     * 创建微信 JSAPI 支付（小程序）
     *
     * @param orderId 订单 ID
     * @param orderNo 订单编号
     * @param amount  实付金额（元）
     * @param userId  用户 ID
     * @param openid  用户微信 openid
     */
    PaymentVO createWechatPayment(Long orderId, String orderNo, BigDecimal amount,
                                  Long userId, String openid);

    /**
     * 处理微信支付 V3 回调
     *
     * @param timestamp 请求头 Wechatpay-Timestamp
     * @param nonce     请求头 Wechatpay-Nonce
     * @param signature 请求头 Wechatpay-Signature
     * @param serial    请求头 Wechatpay-Serial
     * @param body      原始请求体
     * @return 成功返回 null；失败返回错误描述
     */
    String handleWechatNotify(String timestamp, String nonce, String signature,
                               String serial, String body);

    /**
     * 申请退款
     *
     * @param orderId 订单 ID
     * @param amount  退款金额（元）
     */
    boolean refund(Long orderId, BigDecimal amount);
}
