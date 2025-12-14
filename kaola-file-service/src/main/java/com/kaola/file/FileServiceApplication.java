package com.kaola.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.kaola.file", "com.kaola.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.kaola.file.mapper")
public class FileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileServiceApplication.class, args);
        System.out.println("===================================");
        System.out.println("  Kaola File Service 启动成功！");
        System.out.println("  端口: 8093");
        System.out.println("  文档: http://localhost:8093/doc.html");
        System.out.println("===================================");
    }
}
