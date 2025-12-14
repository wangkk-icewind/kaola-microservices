package com.kaola.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 产品服务启动类
 *
 * @author Kaola Team
 */
@SpringBootApplication(scanBasePackages = {"com.kaola.product", "com.kaola.common"})
@EnableDiscoveryClient
@MapperScan("com.kaola.product.mapper")
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
        System.out.println("===================================");
        System.out.println("  Kaola Product Service 启动成功！");
        System.out.println("  端口: 8085");
        System.out.println("  文档: http://localhost:8085/doc.html");
        System.out.println("===================================");
    }
}
