package com.kaola.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证授权服务启动类
 *
 * @author Kaola Team
 */
@SpringBootApplication(scanBasePackages = {"com.kaola.auth", "com.kaola.common"})
@EnableDiscoveryClient
@MapperScan("com.kaola.auth.mapper")
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
        System.out.println("===================================");
        System.out.println("  Kaola Auth Service 启动成功！");
        System.out.println("  端口: 8081");
        System.out.println("  文档: http://localhost:8081/doc.html");
        System.out.println("===================================");
    }
}
