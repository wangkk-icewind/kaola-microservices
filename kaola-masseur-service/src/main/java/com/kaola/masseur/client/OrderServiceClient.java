package com.kaola.masseur.client;

import com.kaola.common.core.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 订单服务Feign客户端
 * 用于获取订单统计信息
 *
 * @author Kaola Team
 */
@FeignClient(name = "kaola-order-service", url = "http://localhost:8086")
public interface OrderServiceClient {

    /**
     * 获取技师的订单数量
     *
     * @param masseurId 技师ID
     * @return 订单数量
     */
    @GetMapping("/order/count")
    Result<Integer> getOrderCountByMasseur(@RequestParam("masseurId") Long masseurId);
}
