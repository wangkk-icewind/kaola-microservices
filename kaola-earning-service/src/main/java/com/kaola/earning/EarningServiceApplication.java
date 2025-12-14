package com.kaola.earning;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.kaola.earning", "com.kaola.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.kaola.earning.mapper")
public class EarningServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EarningServiceApplication.class, args);
        System.out.println("===================================");
        System.out.println("  Kaola Earning Service 启动成功！");
        System.out.println("  端口: 8092");
        System.out.println("  文档: http://localhost:8092/doc.html");
        System.out.println("===================================");
    }
}
