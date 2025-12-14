package com.kaola.payment.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.payment.model.vo.PaymentVO;
import com.kaola.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付接口
 *
 * @author Kaola Team
 */
@Tag(name = "支付接口", description = "微信支付、支付宝支付、退款等接口")
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 创建微信支付
     *
     * @param orderId 订单ID
     * @return 支付信息
     */
    @Operation(summary = "创建微信支付", description = "为指定订单创建微信支付")
    @PostMapping("/wechat/create")
    public Result<PaymentVO> createWechatPayment(
            @Parameter(description = "订单ID", required = true)
            @RequestParam @NotNull(message = "订单ID不能为空") Long orderId) {
        PaymentVO payment = paymentService.createWechatPayment(orderId);
        return Result.success(payment);
    }

    /**
     * 创建支付宝支付
     *
     * @param orderId 订单ID
     * @return 支付信息
     */
    @Operation(summary = "创建支付宝支付", description = "为指定订单创建支付宝支付")
    @PostMapping("/alipay/create")
    public Result<PaymentVO> createAlipayPayment(
            @Parameter(description = "订单ID", required = true)
            @RequestParam @NotNull(message = "订单ID不能为空") Long orderId) {
        PaymentVO payment = paymentService.createAlipayPayment(orderId);
        return Result.success(payment);
    }

    /**
     * 微信支付回调
     *
     * @param xml 回调数据
     * @return 处理结果
     */
    @Operation(summary = "微信支付回调", description = "接收微信支付异步通知")
    @PostMapping("/wechat/notify")
    public String wechatNotify(@RequestBody String xml) {
        return paymentService.handleWechatNotify(xml);
    }

    /**
     * 支付宝回调
     *
     * @param request HTTP请求
     * @return 处理结果
     */
    @Operation(summary = "支付宝回调", description = "接收支付宝支付异步通知")
    @PostMapping("/alipay/notify")
    public String alipayNotify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append((i == values.length - 1) ? values[i] : values[i] + ",");
            }
            params.put(name, valueStr.toString());
        }
        return paymentService.handleAlipayNotify(params);
    }

    /**
     * 退款
     *
     * @param orderId 订单ID
     * @param amount  退款金额
     * @return 操作结果
     */
    @Operation(summary = "退款", description = "为指定订单发起退款")
    @PostMapping("/refund")
    public Result<Boolean> refund(
            @Parameter(description = "订单ID", required = true)
            @RequestParam @NotNull(message = "订单ID不能为空") Long orderId,
            @Parameter(description = "退款金额", required = true)
            @RequestParam @NotNull(message = "退款金额不能为空") BigDecimal amount) {
        boolean success = paymentService.refund(orderId, amount);
        return Result.success(success);
    }
}
