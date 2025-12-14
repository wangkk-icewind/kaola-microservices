package com.kaola.masseur.client;

import com.kaola.common.core.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 门店服务Feign客户端
 * 用于获取门店信息
 *
 * @author Kaola Team
 */
@FeignClient(name = "kaola-store-service")
public interface StoreServiceClient {

    /**
     * 获取门店详情
     *
     * @param id 门店ID
     * @return 门店信息
     */
    @GetMapping("/store/{id}")
    Result<Object> getStoreById(@PathVariable("id") Long id);
}
