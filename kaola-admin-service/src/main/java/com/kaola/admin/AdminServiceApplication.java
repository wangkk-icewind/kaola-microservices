package com.kaola.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.kaola.admin", "com.kaola.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.kaola.admin.mapper")
public class AdminServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
        System.out.println("===================================");
        System.out.println("  Kaola Admin Service 启动成功！");
        System.out.println("  端口: 8095");
        System.out.println("  文档: http://localhost:8095/doc.html");
        System.out.println("===================================");
    }
}
