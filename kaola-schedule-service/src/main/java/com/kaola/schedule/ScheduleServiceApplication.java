package com.kaola.schedule;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.kaola.schedule", "com.kaola.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.kaola.schedule.mapper")
public class ScheduleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleServiceApplication.class, args);
        System.out.println("===================================");
        System.out.println("  Kaola Schedule Service 启动成功！");
        System.out.println("  端口: 8091");
        System.out.println("  文档: http://localhost:8091/doc.html");
        System.out.println("===================================");
    }
}
