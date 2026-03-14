package com.kaola.product.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kaola.product.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kaola.product.model.entity.MasseurLevelPricing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kaola.product.service.MasseurLevelPricingService;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 * 管理后台 - 技师等级价格系数配置接口
 *
 * @author Kaola Team
 */

@Tag(name = "管理后台 - 定价配置", description = "技师等级价格系数管理接口")
@RestController
@RequestMapping("/admin/pricing/masseur-level")
public class AdminMasseurLevelPricingController {

    private static final Logger log = LoggerFactory.getLogger(AdminMasseurLevelPricingController.class);

    private final MasseurLevelPricingService masseurLevelPricingService;

    public AdminMasseurLevelPricingController(MasseurLevelPricingService masseurLevelPricingService) {
        this.masseurLevelPricingService = masseurLevelPricingService;
    }

    /**
     * 获取所有配置列表
     */
    @Operation(summary = "获取配置列表", description = "获取所有技师等级价格系数配置")
    @GetMapping("/list")
    public Result<List<MasseurLevelPricing>> getList() {
        log.info("获取技师等级价格系数配置列表");
        List<MasseurLevelPricing> list = masseurLevelPricingService.getList();
        return Result.success(list);
    }

    /**
     * 获取配置详情
     */
    @Operation(summary = "获取配置详情", description = "根据ID获取配置详情")
    @GetMapping("/{id}")
    public Result<MasseurLevelPricing> getDetail(@PathVariable Long id) {
        log.info("获取技师等级价格系数配置详情, id: {}", id);
        MasseurLevelPricing pricing = masseurLevelPricingService.getById(id);
        if (pricing == null) {
            return Result.error("配置不存在");
        }
        return Result.success(pricing);
    }

    /**
     * 创建配置
     */
    @Operation(summary = "创建配置", description = "新建技师等级价格系数配置")
    @PostMapping
    public Result<Boolean> create(@RequestBody MasseurLevelPricing pricing) {
        log.info("创建技师等级价格系数配置, pricing: {}", pricing);
        try {
            boolean success = masseurLevelPricingService.create(pricing);
            return success ? Result.success(true) : Result.error("创建失败");
        } catch (Exception e) {
            log.error("创建技师等级价格系数配置失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新配置
     */
    @Operation(summary = "更新配置", description = "修改技师等级价格系数配置")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody MasseurLevelPricing pricing) {
        log.info("更新技师等级价格系数配置, id: {}, pricing: {}", id, pricing);
        try {
            boolean success = masseurLevelPricingService.update(id, pricing);
            return success ? Result.success(true) : Result.error("更新失败");
        } catch (Exception e) {
            log.error("更新技师等级价格系数配置失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量更新配置
     */
    @Operation(summary = "批量更新配置", description = "批量修改技师等级价格系数配置")
    @PostMapping("/batch")
    public Result<Boolean> batchUpdate(@RequestBody List<MasseurLevelPricing> pricingList) {
        log.info("批量更新技师等级价格系数配置, count: {}", pricingList.size());
        try {
            boolean success = masseurLevelPricingService.batchUpdate(pricingList);
            return success ? Result.success(true) : Result.error("批量更新失败");
        } catch (Exception e) {
            log.error("批量更新技师等级价格系数配置失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除配置
     */
    @Operation(summary = "删除配置", description = "删除技师等级价格系数配置")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        log.info("删除技师等级价格系数配置, id: {}", id);
        try {
            boolean success = masseurLevelPricingService.delete(id);
            return success ? Result.success(true) : Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除技师等级价格系数配置失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新状态
     */
    @Operation(summary = "更新状态", description = "启用或禁用配置")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        log.info("更新技师等级价格系数配置状态, id: {}, status: {}", id, status);
        try {
            boolean success = masseurLevelPricingService.updateStatus(id, status);
            return success ? Result.success(true) : Result.error("更新状态失败");
        } catch (Exception e) {
            log.error("更新技师等级价格系数配置状态失败", e);
            return Result.error(e.getMessage());
        }
    }
}
