package com.kaola.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 支付配置类
 * 配置微信支付和支付宝支付
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "payment")
public class PaymentConfig {

    /**
     * 微信支付配置
     */
    private WechatPayConfig wechat;

    /**
     * 支付宝支付配置
     */
    private AlipayConfig alipay;

    /**
     * 微信支付配置项
     */
    @Data
    public static class WechatPayConfig {
        /**
         * 小程序AppID
         */
        private String appId;

        /**
         * 商户号
         */
        private String mchId;

        /**
         * API密钥
         */
        private String apiKey;

        /**
         * 支付回调通知URL
         */
        private String notifyUrl;

        /**
         * 证书路径（可选）
         */
        private String certPath;

        /**
         * 证书密码（可选）
         */
        private String certPassword;
    }

    /**
     * 支付宝支付配置项
     */
    @Data
    public static class AlipayConfig {
        /**
         * 应用ID
         */
        private String appId;

        /**
         * 应用私钥
         */
        private String privateKey;

        /**
         * 支付宝公钥
         */
        private String alipayPublicKey;

        /**
         * 支付回调通知URL
         */
        private String notifyUrl;

        /**
         * 网关地址
         */
        private String serverUrl = "https://openapi.alipay.com/gateway.do";

        /**
         * 签名类型
         */
        private String signType = "RSA2";

        /**
         * 字符编码
         */
        private String charset = "UTF-8";

        /**
         * 返回格式
         */
        private String format = "json";
    }

    /**
     * 获取微信支付AppID
     */
    public String getWechatAppId() {
        return wechat != null ? wechat.getAppId() : null;
    }

    /**
     * 获取微信支付商户号
     */
    public String getWechatMchId() {
        return wechat != null ? wechat.getMchId() : null;
    }

    /**
     * 获取支付宝AppID
     */
    public String getAlipayAppId() {
        return alipay != null ? alipay.getAppId() : null;
    }
}
