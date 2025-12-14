package com.kaola.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 考拉推拿 API 网关
 * 
 * @author Kaola Team
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("\n" +
                "===================================\n" +
                "  Kaola Gateway 启动成功！\n" +
                "  端口: 8080\n" +
                "  健康检查: http://localhost:8080/actuator/health\n" +
                "  路由信息: http://localhost:8080/actuator/gateway/routes\n" +
                "===================================\n");
    }
}
