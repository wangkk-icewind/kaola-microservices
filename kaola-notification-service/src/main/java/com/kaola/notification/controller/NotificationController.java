package com.kaola.notification.controller;

import com.kaola.notification.model.dto.Result;
import com.kaola.notification.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 通知服务接口
 *
 * @author Kaola Team
 */
@Tag(name = "通知服务", description = "短信验证码、推送通知等接口")
@RestController
@RequestMapping("/notification")
@Validated
public class NotificationController {

    @Autowired
    private SmsService smsService;

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @return 操作结果
     */
    @Operation(summary = "发送验证码", description = "向指定手机号发送短信验证码")
    @PostMapping("/sms/send")
    public Result<Boolean> sendVerifyCode(
            @Parameter(description = "手机号", required = true)
            @RequestParam
            @NotBlank(message = "手机号不能为空")
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
            String phone) {
        boolean success = smsService.sendVerifyCode(phone);
        return Result.success(success);
    }

    /**
     * 验证验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 验证结果
     */
    @Operation(summary = "验证验证码", description = "验证短信验证码是否正确")
    @PostMapping("/sms/verify")
    public Result<Boolean> verifyCode(
            @Parameter(description = "手机号", required = true)
            @RequestParam
            @NotBlank(message = "手机号不能为空")
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
            String phone,
            @Parameter(description = "验证码", required = true)
            @RequestParam
            @NotBlank(message = "验证码不能为空")
            @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
            String code) {
        boolean success = smsService.verifyCode(phone, code);
        return Result.success(success);
    }
}
