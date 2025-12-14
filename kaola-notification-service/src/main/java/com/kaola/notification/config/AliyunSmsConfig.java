package com.kaola.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云短信配置类
 * 用于发送验证码等短信服务
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.sms")
public class AliyunSmsConfig {

    /**
     * 访问密钥ID
     */
    private String accessKeyId;

    /**
     * 访问密钥Secret
     */
    private String accessKeySecret;

    /**
     * 短信签名
     */
    private String signName;

    /**
     * 短信模板编码
     */
    private String templateCode;

    /**
     * 短信服务端点（默认）
     */
    private String endpoint = "dysmsapi.aliyuncs.com";

    /**
     * 短信服务区域（默认）
     */
    private String regionId = "cn-hangzhou";
}
