package com.kaola.store;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.kaola.store", "com.kaola.common"})
@EnableDiscoveryClient
@MapperScan("com.kaola.store.mapper")
public class StoreServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoreServiceApplication.class, args);
        System.out.println("===================================");
        System.out.println("  Kaola Store Service 启动成功！");
        System.out.println("  端口: 8083");
        System.out.println("  文档: http://localhost:8083/doc.html");
        System.out.println("===================================");
    }
}
