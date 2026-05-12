package com.kaola.order.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaola.common.core.dto.Result;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.math.BigDecimal;

@FeignClient(name = "kaola-payment-service", url = "${feign.payment-service.url:http://localhost:8087}")
public interface PaymentServiceClient {

    @PostMapping("/payment/wechat/create")
    Result<PaymentResult> createWechatPayment(
            @RequestParam("orderId") Long orderId,
            @RequestParam("orderNo") String orderNo,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("userId") Long userId,
            @RequestParam("openid") String openid);

    /** payment-service PaymentVO 的本地镜像（只取需要的字段） */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    class PaymentResult implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long id;
        private String paymentNo;
        private WxParams wxPayParams;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class WxParams implements Serializable {
            private static final long serialVersionUID = 1L;
            private String appId;
            private String timeStamp;
            private String nonceStr;
            @JsonProperty("package")
            private String packageValue;
            private String signType;
            private String paySign;
        }
    }
}
