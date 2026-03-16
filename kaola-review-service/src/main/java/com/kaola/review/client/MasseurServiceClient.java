package com.kaola.review.client;

import com.kaola.common.core.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "kaola-masseur-service-review", url = "http://localhost:8084")
public interface MasseurServiceClient {

    @GetMapping("/masseur/detail/{id}")
    Result<MasseurVO> getMasseurDetail(@PathVariable Long id);

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    class MasseurVO {
        private Long id;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
