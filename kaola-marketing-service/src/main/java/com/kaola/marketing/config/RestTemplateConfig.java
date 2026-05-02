package com.kaola.marketing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.Proxy;

/**
 * RestTemplate 配置
 * 显式配置不走系统代理，确保 localhost 调用不走 http_proxy
 */
@Configuration
public class RestTemplateConfig {

    @Value("${resttemplate.proxy.enabled:false}")
    private boolean proxyEnabled;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 显式禁用代理，确保 localhost 调用不走系统代理
        if (!proxyEnabled) {
            factory.setProxy(Proxy.NO_PROXY);
        }
        return new RestTemplate(factory);
    }
}
