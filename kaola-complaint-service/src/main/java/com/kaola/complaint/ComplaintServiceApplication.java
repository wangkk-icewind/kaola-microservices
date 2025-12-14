package com.kaola.complaint;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.kaola.complaint", "com.kaola.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.kaola.complaint.mapper")
public class ComplaintServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComplaintServiceApplication.class, args);
        System.out.println("===================================");
        System.out.println("  Kaola Complaint Service 启动成功！");
        System.out.println("  端口: 8090");
        System.out.println("  文档: http://localhost:8090/doc.html");
        System.out.println("===================================");
    }
}
