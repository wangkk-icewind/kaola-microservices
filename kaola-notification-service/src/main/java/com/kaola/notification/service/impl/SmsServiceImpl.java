package com.kaola.notification.service.impl;

import com.kaola.notification.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务实现类
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final StringRedisTemplate redisTemplate;

    @Value("${sms.verify-code.expire-minutes:5}")
    private int expireMinutes;

    @Value("${sms.verify-code.length:6}")
    private int codeLength;

    private static final String SMS_CODE_PREFIX = "sms:code:";
    private static final String SMS_SEND_LIMIT_PREFIX = "sms:limit:";

    @Override
    public boolean sendVerifyCode(String phone) {
        log.info("发送验证码, phone: {}", phone);

        // 检查发送频率限制 (1分钟内只能发送一次)
        String limitKey = SMS_SEND_LIMIT_PREFIX + phone;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(limitKey))) {
            throw new RuntimeException("发送过于频繁，请稍后再试");
        }

        // 生成验证码
        String code = generateCode();

        // 存储验证码
        String codeKey = SMS_CODE_PREFIX + phone;
        redisTemplate.opsForValue().set(codeKey, code, expireMinutes, TimeUnit.MINUTES);

        // 设置发送频率限制
        redisTemplate.opsForValue().set(limitKey, "1", 60, TimeUnit.SECONDS);

        // TODO: 调用短信服务商API发送短信
        // 这里使用阿里云短信服务示例
        boolean sendResult = sendSms(phone, code);

        if (!sendResult) {
            // 发送失败，删除验证码
            redisTemplate.delete(codeKey);
            throw new RuntimeException("短信发送失败，请稍后再试");
        }

        log.info("验证码发送成功, phone: {}, code: {}", phone, code);
        return true;
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        log.info("验证验证码, phone: {}, code: {}", phone, code);

        String codeKey = SMS_CODE_PREFIX + phone;
        String savedCode = redisTemplate.opsForValue().get(codeKey);

        if (savedCode == null) {
            throw new RuntimeException("验证码已过期");
        }

        if (!savedCode.equals(code)) {
            return false;
        }

        // 验证成功，删除验证码
        redisTemplate.delete(codeKey);

        return true;
    }

    /**
     * 生成验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 发送短信
     * TODO: 接入阿里云短信服务
     */
    private boolean sendSms(String phone, String code) {
        // 这里应该调用阿里云短信服务API
        // 示例代码:
        /*
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "考拉预约");
        request.putQueryParameter("TemplateCode", "SMS_123456789");
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");

        CommonResponse response = client.getCommonResponse(request);
        return response.getHttpStatus() == 200;
        */

        // 模拟发送成功
        log.info("模拟发送短信 - phone: {}, code: {}", phone, code);
        return true;
    }
}
