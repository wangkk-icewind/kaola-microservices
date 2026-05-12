package com.kaola.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "payment")
public class PaymentConfig {

    private WechatPayConfig wechat = new WechatPayConfig();

    @Data
    public static class WechatPayConfig {
        /** 小程序 AppID */
        private String appId;
        /** 商户号 */
        private String mchId;
        /** 商户证书序列号 */
        private String mchSerialNo;
        /** APIv3 密钥（32字节） */
        private String apiV3Key;
        /** 商户私钥内容（PEM 格式，与 privateKeyPath 二选一） */
        private String privateKey;
        /** 商户私钥文件路径（与 privateKey 二选一） */
        private String privateKeyPath;
        /** 支付回调通知 URL */
        private String notifyUrl;
    }
}
