package com.kaola.masseur.client;

import com.kaola.common.core.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 项目服务Feign客户端
 * 用于获取项目/服务信息
 *
 * @author Kaola Team
 */
@FeignClient(name = "kaola-product-service", url = "http://localhost:8085")
public interface ProjectServiceClient {

    /**
     * 获取技师可提供的服务项目列表
     *
     * @param masseurId 技师ID
     * @return 服务项目列表
     */
    @GetMapping("/project/masseur/{masseurId}")
    Result<List<Object>> getProjectsByMasseur(@PathVariable("masseurId") Long masseurId);

    /**
     * 获取项目详情
     *
     * @param id 项目ID
     * @return 项目信息
     */
    @GetMapping("/project/{id}")
    Result<Object> getProjectById(@PathVariable("id") Long id);
}
