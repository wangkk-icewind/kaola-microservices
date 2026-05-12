package com.kaola.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 调用 admin-service 内部接口获取支付配置（group=payment）
 */
@FeignClient(name = "kaola-admin-service", url = "${feign.admin-service.url:http://localhost:8091}")
public interface AdminServiceClient {

    @GetMapping("/admin/settings/payment/internal/group/{group}")
    Map<String, String> getGroupConfig(@PathVariable("group") String group);

    default Map<String, String> getPaymentConfig() {
        return getGroupConfig("payment");
    }
}
