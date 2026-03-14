package com.kaola.masseur;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 技师服务启动类
 *
 * @author Kaola Team
 */
@EnableCaching
@SpringBootApplication(scanBasePackages = {"com.kaola.masseur", "com.kaola.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.kaola.masseur.mapper")
public class MasseurServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MasseurServiceApplication.class, args);
        System.out.println("===================================");
        System.out.println("  Kaola Masseur Service 启动成功！");
        System.out.println("  端口: 8084");
        System.out.println("  文档: http://localhost:8084/doc.html");
        System.out.println("===================================");
    }
}
