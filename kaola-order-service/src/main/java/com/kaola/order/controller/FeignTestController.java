package com.kaola.order.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.order.client.MasseurServiceClient;
import com.kaola.order.client.ProductServiceClient;
import com.kaola.order.client.StoreServiceClient;
import com.kaola.order.client.UserServiceClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Feign 测试接口
 * 用于验证跨服务调用功能
 *
 * @author Kaola Team
 */
@Tag(name = "Feign测试接口", description = "测试跨服务调用功能")
@RestController
@RequestMapping("/test/feign")
@RequiredArgsConstructor
public class FeignTestController {

    private final UserServiceClient userServiceClient;
    private final StoreServiceClient storeServiceClient;
    private final MasseurServiceClient masseurServiceClient;
    private final ProductServiceClient productServiceClient;

    /**
     * 测试调用用户服务
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Operation(summary = "测试用户服务", description = "通过Feign调用用户服务获取用户信息")
    @GetMapping("/user/{userId}")
    public Result<?> testUserService(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        return userServiceClient.getUserInfo(userId);
    }

    /**
     * 测试调用门店服务
     *
     * @param storeId 门店ID
     * @return 门店信息
     */
    @Operation(summary = "测试门店服务", description = "通过Feign调用门店服务获取门店信息")
    @GetMapping("/store/{storeId}")
    public Result<?> testStoreService(
            @Parameter(description = "门店ID", required = true)
            @PathVariable Long storeId) {
        return storeServiceClient.getStoreDetail(storeId);
    }

    /**
     * 测试调用技师服务
     *
     * @param masseurId 技师ID
     * @return 技师信息
     */
    @Operation(summary = "测试技师服务", description = "通过Feign调用技师服务获取技师信息")
    @GetMapping("/masseur/{masseurId}")
    public Result<?> testMasseurService(
            @Parameter(description = "技师ID", required = true)
            @PathVariable Long masseurId) {
        return masseurServiceClient.getMasseurDetail(masseurId);
    }

    /**
     * 测试调用项目服务 - 获取项目列表
     *
     * @param categoryId 分类ID
     * @return 项目列表
     */
    @Operation(summary = "测试项目服务", description = "通过Feign调用项目服务获取项目列表")
    @GetMapping("/project/list")
    public Result<?> testProductService(
            @Parameter(description = "分类ID")
            @RequestParam(required = false) Long categoryId) {
        return productServiceClient.getProjectList(categoryId, null);
    }

    /**
     * 测试所有服务
     *
     * @return 测试结果
     */
    @Operation(summary = "测试所有服务", description = "测试所有Feign客户端连接")
    @GetMapping("/all")
    public Result<String> testAllServices() {
        StringBuilder result = new StringBuilder();
        result.append("=== Feign 跨服务调用测试 ===\n\n");

        // 测试用户服务
        try {
            Result<?> userResult = userServiceClient.getUserInfo(1L);
            result.append("✅ 用户服务: 连接成功\n");
            result.append("   返回状态: ").append(userResult.getCode()).append("\n\n");
        } catch (Exception e) {
            result.append("❌ 用户服务: 连接失败\n");
            result.append("   错误: ").append(e.getMessage()).append("\n\n");
        }

        // 测试门店服务
        try {
            Result<?> storeResult = storeServiceClient.getStoreDetail(1L);
            result.append("✅ 门店服务: 连接成功\n");
            result.append("   返回状态: ").append(storeResult.getCode()).append("\n\n");
        } catch (Exception e) {
            result.append("❌ 门店服务: 连接失败\n");
            result.append("   错误: ").append(e.getMessage()).append("\n\n");
        }

        // 测试技师服务
        try {
            Result<?> masseurResult = masseurServiceClient.getMasseurDetail(1L);
            result.append("✅ 技师服务: 连接成功\n");
            result.append("   返回状态: ").append(masseurResult.getCode()).append("\n\n");
        } catch (Exception e) {
            result.append("❌ 技师服务: 连接失败\n");
            result.append("   错误: ").append(e.getMessage()).append("\n\n");
        }

        // 测试项目服务
        try {
            Result<?> productResult = productServiceClient.getProjectList(null, null);
            result.append("✅ 项目服务: 连接成功\n");
            result.append("   返回状态: ").append(productResult.getCode()).append("\n\n");
        } catch (Exception e) {
            result.append("❌ 项目服务: 连接失败\n");
            result.append("   错误: ").append(e.getMessage()).append("\n\n");
        }

        result.append("=== 测试完成 ===");
        return Result.success(result.toString());
    }
}
