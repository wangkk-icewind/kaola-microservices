package com.kaola.product.controller;

import com.kaola.product.dto.Result;
import com.kaola.product.model.vo.PricingConfigVO;
import com.kaola.product.service.PricingConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定价配置控制器 - 用户端API
 * 用于小程序端获取定价配置
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "定价配置", description = "定价配置相关接口（用于小程序端）")
@RestController
@RequestMapping("/price")
@RequiredArgsConstructor
public class PriceConfigController {

    private final PricingConfigService pricingConfigService;

    @Operation(summary = "获取定价配置", description = "返回技师等级、时段、门店的价格系数配置")
    @GetMapping("/config")
    public Result<PricingConfigVO> getPricingConfig() {
        log.info("API调用: 获取定价配置");
        PricingConfigVO config = pricingConfigService.getPricingConfig();
        return Result.success(config);
    }
}
