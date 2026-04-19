package com.kaola.masseur.client;

import com.kaola.common.core.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 评价服务Feign客户端
 * 用于获取评价数据和统计信息
 *
 * @author Kaola Team
 */
@FeignClient(name = "kaola-review-service", url = "http://localhost:8093")
public interface ReviewServiceClient {

    /**
     * 获取技师的评价数量
     *
     * @param masseurId 技师ID
     * @return 评价数量
     */
    @GetMapping("/review/count")
    Result<Integer> getReviewCountByMasseur(@RequestParam("masseurId") Long masseurId);

    /**
     * 获取技师的平均评分
     *
     * @param masseurId 技师ID
     * @return 平均评分
     */
    @GetMapping("/review/rating")
    Result<Double> getAverageRatingByMasseur(@RequestParam("masseurId") Long masseurId);
}
