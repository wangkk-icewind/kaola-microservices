package com.kaola.product.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kaola.product.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kaola.product.dto.PriceCalculationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kaola.product.dto.PriceCalculationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kaola.product.service.PriceCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台 - 价格计算接口
 *
 * @author Kaola Team
 */

@Tag(name = "管理后台 - 价格计算", description = "订单价格计算和预览接口")
@RestController
@RequestMapping("/admin/price")
public class AdminPriceCalculationController {

    private static final Logger log = LoggerFactory.getLogger(AdminPriceCalculationController.class);

    private final PriceCalculationService priceCalculationService;

    public AdminPriceCalculationController(PriceCalculationService priceCalculationService) {
        this.priceCalculationService = priceCalculationService;
    }

    /**
     * 预览价格
     */
    @Operation(summary = "预览价格", description = "根据项目、技师、门店等信息预览价格")
    @PostMapping("/preview")
    public Result<PriceCalculationResult> previewPrice(@RequestBody PriceCalculationRequest request) {
        log.info("预览价格, request: {}", request);
        try {
            PriceCalculationResult result = priceCalculationService.previewPrice(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("价格预览失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 计算价格
     */
    @Operation(summary = "计算价格", description = "计算订单实际价格")
    @PostMapping("/calculate")
    public Result<PriceCalculationResult> calculatePrice(@RequestBody PriceCalculationRequest request) {
        log.info("计算价格, request: {}", request);
        try {
            PriceCalculationResult result = priceCalculationService.calculatePrice(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("价格计算失败", e);
            return Result.error(e.getMessage());
        }
    }
}
