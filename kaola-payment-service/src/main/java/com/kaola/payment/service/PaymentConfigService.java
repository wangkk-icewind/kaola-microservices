package com.kaola.payment.service;

import com.kaola.payment.client.AdminServiceClient;
import com.kaola.payment.config.PaymentConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 动态读取支付配置：优先从 admin-service 的 t_system_setting(group=payment) 加载，
 * 结果缓存到 Redis（TTL 5 分钟），缺失字段回退到 application.yml。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentConfigService {

    private static final String CACHE_KEY = "payment:config:wechat";
    private static final long CACHE_TTL_SECONDS = 300;

    private final AdminServiceClient adminServiceClient;
    private final StringRedisTemplate redisTemplate;
    private final PaymentConfig fallback;

    /**
     * 返回合并后的微信支付配置（动态优先，yml 兜底）。
     */
    public PaymentConfig.WechatPayConfig getWechatConfig() {
        try {
            Map<String, String> remote = fetchFromAdminWithCache();
            PaymentConfig.WechatPayConfig merged = new PaymentConfig.WechatPayConfig();

            merged.setAppId(firstNonBlank(remote.get("wechat.appId"), fallback.getWechat().getAppId()));
            merged.setMchId(firstNonBlank(remote.get("wechat.mchId"), fallback.getWechat().getMchId()));
            merged.setMchSerialNo(firstNonBlank(remote.get("wechat.mchSerialNo"), fallback.getWechat().getMchSerialNo()));
            merged.setApiV3Key(firstNonBlank(remote.get("wechat.apiV3Key"), fallback.getWechat().getApiV3Key()));
            merged.setPrivateKey(firstNonBlank(remote.get("wechat.privateKey"), fallback.getWechat().getPrivateKey()));
            merged.setPrivateKeyPath(firstNonBlank(remote.get("wechat.privateKeyPath"), fallback.getWechat().getPrivateKeyPath()));
            merged.setNotifyUrl(firstNonBlank(remote.get("wechat.notifyUrl"), fallback.getWechat().getNotifyUrl()));

            return merged;
        } catch (Exception e) {
            log.warn("获取远程支付配置失败，使用 yml 兜底: {}", e.getMessage());
            return fallback.getWechat();
        }
    }

    /** 主动失效缓存（后台更新配置后调用）。 */
    public void invalidateCache() {
        redisTemplate.delete(CACHE_KEY);
        log.info("支付配置缓存已清除");
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> fetchFromAdminWithCache() {
        // 尝试从 Redis 取
        Object cached = redisTemplate.opsForHash().entries(CACHE_KEY);
        if (cached instanceof Map<?, ?> map && !map.isEmpty()) {
            return (Map<String, String>) map;
        }

        // 从 admin-service 拉取 group=payment 的配置
        Map<String, String> remote = adminServiceClient.getPaymentConfig();
        if (remote != null && !remote.isEmpty()) {
            redisTemplate.opsForHash().putAll(CACHE_KEY, remote);
            redisTemplate.expire(CACHE_KEY, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        }
        return remote != null ? remote : Map.of();
    }

    private String firstNonBlank(String... values) {
        for (String v : values) {
            if (StringUtils.hasText(v)) return v;
        }
        return null;
    }
}
