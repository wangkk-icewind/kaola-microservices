package com.kaola.admin.controller;

import com.kaola.admin.service.SmsService;
import com.kaola.common.core.dto.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 短信发送（内部接口，由 user/masseur/marketing 服务调用；不经网关对外）。
 */
@Slf4j
@Tag(name = "短信服务")
@RestController
@RequestMapping("/admin/sms")
@RequiredArgsConstructor
public class AdminSmsController {

    private final SmsService smsService;

    @Operation(summary = "发送验证码", description = "生成验证码并发送，存入 Redis sms:code:{phone}")
    @PostMapping("/send-verify")
    public Result<Boolean> sendVerify(@RequestParam String phone) {
        try {
            return Result.success(smsService.sendVerifyCode(phone));
        } catch (Exception e) {
            log.warn("发送验证码失败 phone={}, err={}", phone, e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "发送优惠券通知", description = "券派发时发送优惠券到账短信")
    @PostMapping("/send-coupon")
    public Result<Boolean> sendCoupon(@RequestParam String phone,
                                      @RequestParam(required = false) String couponName) {
        return Result.success(smsService.sendCouponNotify(phone, couponName));
    }

    @Operation(summary = "发送预约成功通知", description = "订单支付成功后发送预约成功短信")
    @PostMapping("/send-booking")
    public Result<Boolean> sendBooking(@RequestBody java.util.Map<String, String> body) {
        return Result.success(smsService.sendBookingSuccess(
                body.get("phone"), body.get("store"), body.get("time"),
                body.get("service"), body.get("name")));
    }

    @Operation(summary = "发送服务后返券通知", description = "服务完成返券时发送短信")
    @PostMapping("/send-return")
    public Result<Boolean> sendReturn(@RequestParam String phone, @RequestParam String sum) {
        return Result.success(smsService.sendReturnCoupon(phone, sum));
    }
}
