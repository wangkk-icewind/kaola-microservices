package com.kaola.payment.client;

import com.kaola.common.core.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 通知 order-service 支付结果（回调后更新订单状态）
 */
@FeignClient(name = "kaola-order-service", url = "${feign.order-service.url:http://localhost:8086}")
public interface OrderServiceClient {

    /**
     * 通知订单支付成功（内部接口，绕过网关鉴权）
     *
     * @param orderNo       订单编号
     * @param transactionId 微信交易号
     */
    @PostMapping("/order/internal/paid")
    Result<Boolean> notifyOrderPaid(@RequestParam("orderNo") String orderNo,
                                    @RequestParam("transactionId") String transactionId);
}
