package com.kaola.payment.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.payment.model.vo.PaymentVO;
import com.kaola.payment.service.PaymentConfigService;
import com.kaola.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Tag(name = "支付接口", description = "微信支付、退款等接口")
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Validated
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentConfigService paymentConfigService;

    @Operation(summary = "创建微信 JSAPI 支付", description = "由 order-service 内部调用，返回小程序调起支付的参数")
    @PostMapping("/wechat/create")
    public Result<PaymentVO> createWechatPayment(
            @RequestParam @NotNull Long orderId,
            @RequestParam @NotBlank String orderNo,
            @RequestParam @NotNull BigDecimal amount,
            @RequestParam @NotNull Long userId,
            @RequestParam @NotBlank String openid) {
        PaymentVO vo = paymentService.createWechatPayment(orderId, orderNo, amount, userId, openid);
        return Result.success(vo);
    }

    /**
     * 微信支付 V3 回调通知。
     * 微信服务器 POST 到此地址，携带 JSON 密文体和签名请求头。
     * 返回 HTTP 200 + JSON {"code":"SUCCESS"} 表示处理成功；
     * 其他返回值微信会重试。
     */
    @Operation(summary = "微信支付 V3 回调通知")
    @PostMapping("/wechat/notify")
    public Map<String, String> wechatNotify(
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Nonce") String nonce,
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestHeader("Wechatpay-Serial") String serial,
            @RequestBody String body) {

        String error = paymentService.handleWechatNotify(timestamp, nonce, signature, serial, body);
        if (error == null) {
            return Map.of("code", "SUCCESS", "message", "成功");
        }
        log.error("微信回调处理失败: {}", error);
        return Map.of("code", "FAIL", "message", error);
    }

    @Operation(summary = "申请退款")
    @PostMapping("/refund")
    public Result<Boolean> refund(
            @Parameter(description = "订单 ID") @RequestParam @NotNull Long orderId,
            @Parameter(description = "退款金额（元）") @RequestParam @NotNull BigDecimal amount) {
        try {
            return Result.success(paymentService.refund(orderId, amount));
        } catch (com.wechat.pay.java.core.exception.ServiceException e) {
            // 透出微信真实退款错误（如 NOT_ENOUGH 余额不足），避免被 Feign 包成 "No fallback available"
            log.error("微信退款失败, code={}, msg={}", e.getErrorCode(), e.getErrorMessage());
            return Result.error("微信退款失败：" + e.getErrorMessage());
        } catch (Exception e) {
            log.error("退款失败, orderId={}", orderId, e);
            return Result.error(e.getMessage() != null ? e.getMessage() : "退款失败");
        }
    }

    @Operation(summary = "刷新支付配置缓存", description = "后台更新支付参数后调用，立即生效")
    @PostMapping("/config/refresh")
    public Result<Boolean> refreshConfig() {
        paymentConfigService.invalidateCache();
        return Result.success(true);
    }
}
