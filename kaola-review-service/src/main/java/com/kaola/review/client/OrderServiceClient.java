package com.kaola.review.client;

import com.kaola.common.core.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "kaola-order-service-review", url = "http://localhost:8086")
public interface OrderServiceClient {

    @GetMapping("/admin/order/detail/{id}")
    Result<OrderVO> getOrderDetail(@PathVariable Long id);

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    class OrderVO {
        private Long id;
        private String orderNo;
        private String projectName;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }
    }
}
