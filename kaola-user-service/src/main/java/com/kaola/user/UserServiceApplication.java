package com.kaola.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用户服务启动类
 *
 * @author Kaola Team
 */
@SpringBootApplication(scanBasePackages = {"com.kaola.user", "com.kaola.common"})
@EnableDiscoveryClient
@MapperScan("com.kaola.user.mapper")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        System.out.println("===================================");
        System.out.println("  Kaola User Service 启动成功！");
        System.out.println("  端口: 8082");
        System.out.println("  文档: http://localhost:8082/doc.html");
        System.out.println("===================================");
    }
}
