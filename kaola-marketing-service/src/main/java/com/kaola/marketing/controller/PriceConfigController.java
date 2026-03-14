package com.kaola.marketing.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.marketing.model.dto.PriceCalculationRequest;
import com.kaola.marketing.model.vo.PriceCalculationVO;
import com.kaola.marketing.model.vo.PricingConfigVO;
import com.kaola.marketing.service.PricingConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 定价配置控制器 - 用户端API
 * 用于小程序端获取定价配置和计算价格
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "定价配置", description = "定价配置相关接口（用于小程序端）")
@RestController
@RequestMapping("/price")
@RequiredArgsConstructor
@Validated
public class PriceConfigController {

    private final PricingConfigService pricingConfigService;

    @Operation(summary = "获取定价配置", description = "获取技师等级、时段、门店的定价配置")
    @GetMapping("/config")
    public Result<PricingConfigVO> getPricingConfig() {
        log.info("API调用: 获取定价配置");
        try {
            PricingConfigVO config = pricingConfigService.getPricingConfig();
            return Result.success(config);
        } catch (Exception e) {
            log.error("获取定价配置失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "计算价格", description = "根据项目、技师等级、时段、门店等因素计算实时价格")
    @PostMapping("/calculate")
    public Result<PriceCalculationVO> calculatePrice(@Valid @RequestBody PriceCalculationRequest request) {
        log.info("API调用: 计算价格 - projectId={}, masseurLevel={}, storeId={}",
                request.getProjectId(), request.getMasseurLevel(), request.getStoreId());
        try {
            PriceCalculationVO result = pricingConfigService.calculatePrice(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("计算价格失败", e);
            return Result.error(e.getMessage());
        }
    }
}
