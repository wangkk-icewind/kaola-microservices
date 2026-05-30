package com.kaola.admin.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.kaola.admin.mapper.SystemSettingMapper;
import com.kaola.admin.model.entity.SystemSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务：从 t_system_setting 读阿里云配置，统一发送验证码/通知短信。
 * 凭证缺失时回退 mock（验证码固定 123456），便于开发环境不被阻断。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final SystemSettingMapper settingMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String CODE_KEY_PREFIX = "sms:code:";
    private static final String LIMIT_KEY_PREFIX = "sms:limit:";
    private static final long CODE_TTL_MINUTES = 5;
    private static final long LIMIT_SECONDS = 60;
    private static final String MOCK_CODE = "123456";

    /**
     * 发送验证码：频率限制 → 生成码 → 发送（或 mock）→ 存 Redis。
     * 失败抛异常（由上层返回错误）。
     */
    public boolean sendVerifyCode(String phone) {
        validatePhone(phone);

        String limitKey = LIMIT_KEY_PREFIX + phone;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(limitKey))) {
            throw new RuntimeException("发送过于频繁，请稍后再试");
        }

        String ak = cfg("sms_access_key");
        String sk = cfg("sms_secret");
        String sign = cfg("sms_sign_name");
        String template = cfg("sms_template_verify");

        String code = genCode();
        boolean credsReady = StringUtils.hasText(ak) && StringUtils.hasText(sk)
                && StringUtils.hasText(sign) && StringUtils.hasText(template);

        if (credsReady) {
            boolean ok = sendSms(ak, sk, phone, sign, template, "{\"code\":\"" + code + "\"}");
            if (!ok) {
                throw new RuntimeException("短信发送失败，请稍后再试");
            }
        } else {
            // 凭证未配置：开发回退，验证码固定 123456
            code = MOCK_CODE;
            log.warn("短信凭证未配置，回退 mock 验证码 phone={}, code={}", phone, code);
        }

        redisTemplate.opsForValue().set(CODE_KEY_PREFIX + phone, code, CODE_TTL_MINUTES, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(limitKey, "1", LIMIT_SECONDS, TimeUnit.SECONDS);
        log.info("验证码已发送, phone={}, 渠道={}", phone, credsReady ? "aliyun" : "mock");
        return true;
    }

    /**
     * 发送优惠券通知短信（券派发时）。失败仅记日志，不抛异常（不阻断派发）。
     */
    public boolean sendCouponNotify(String phone, String couponName) {
        try {
            validatePhone(phone);
            String ak = cfg("sms_access_key");
            String sk = cfg("sms_secret");
            String sign = cfg("sms_sign_name");
            String template = cfg("sms_template_coupon");
            if (!StringUtils.hasText(ak) || !StringUtils.hasText(sk)
                    || !StringUtils.hasText(sign) || !StringUtils.hasText(template)) {
                log.warn("优惠券短信凭证/模板未配置，跳过发送 phone={}", phone);
                return false;
            }
            String param = "{\"name\":\"" + (couponName != null ? couponName : "优惠券") + "\"}";
            return sendSms(ak, sk, phone, sign, template, param);
        } catch (Exception e) {
            log.error("优惠券短信发送异常 phone={}", phone, e);
            return false;
        }
    }

    /** 调用阿里云 SendSms，Code==OK 视为成功。 */
    private boolean sendSms(String ak, String sk, String phone, String sign, String template, String param) {
        try {
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", ak, sk);
            IAcsClient client = new DefaultAcsClient(profile);
            SendSmsRequest request = new SendSmsRequest();
            request.setPhoneNumbers(phone);
            request.setSignName(sign);
            request.setTemplateCode(template);
            request.setTemplateParam(param);
            SendSmsResponse response = client.getAcsResponse(request);
            if ("OK".equals(response.getCode())) {
                return true;
            }
            log.error("阿里云短信返回非 OK: code={}, message={}, phone={}",
                    response.getCode(), response.getMessage(), phone);
            return false;
        } catch (Exception e) {
            log.error("阿里云短信发送异常 phone={}", phone, e);
            return false;
        }
    }

    private String cfg(String key) {
        SystemSetting s = settingMapper.findByKey(key);
        return s != null ? s.getSettingValue() : null;
    }

    private String genCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) sb.append(random.nextInt(10));
        return sb.toString();
    }

    private void validatePhone(String phone) {
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }
    }
}
