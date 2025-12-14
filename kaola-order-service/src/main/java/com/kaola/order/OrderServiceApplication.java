package com.kaola.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 订单服务启动类
 *
 * @author Kaola Team
 */
@SpringBootApplication(scanBasePackages = {"com.kaola.order", "com.kaola.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.kaola.order.mapper")
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("===================================");
        System.out.println("  Kaola Order Service 启动成功！");
        System.out.println("  端口: 8086");
        System.out.println("  文档: http://localhost:8086/doc.html");
        System.out.println("===================================");
    }
}
