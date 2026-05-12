package com.kaola.order.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部服务间调用接口（绕过网关鉴权，不对外暴露）
 */
@Slf4j
@RestController
@RequestMapping("/order/internal")
@RequiredArgsConstructor
public class OrderInternalController {

    private final OrderService orderService;

    /**
     * 微信支付成功回调（由 payment-service 调用）
     *
     * @param orderNo       订单编号
     * @param transactionId 微信交易号
     */
    @PostMapping("/paid")
    public Result<Boolean> markPaid(@RequestParam String orderNo,
                                    @RequestParam String transactionId) {
        log.info("收到支付成功通知, orderNo={}, transactionId={}", orderNo, transactionId);
        boolean success = orderService.markOrderPaid(orderNo, transactionId);
        return Result.success(success);
    }
}
