package com.kaola.product.client;

import com.kaola.product.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 技师服务Feign客户端
 *
 * @author Kaola Team
 */
@FeignClient(name = "kaola-masseur-service")
public interface MasseurServiceClient {

    /**
     * 获取技师的项目ID列表
     *
     * @param masseurId 技师ID
     * @return 项目ID列表
     */
    @GetMapping("/masseur/{masseurId}/projects")
    Result<List<Long>> getMasseurProjects(@PathVariable("masseurId") Long masseurId);
}
