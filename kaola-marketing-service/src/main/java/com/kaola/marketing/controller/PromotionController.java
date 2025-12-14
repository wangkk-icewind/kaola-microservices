package com.kaola.marketing.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.marketing.model.vo.CouponVO;
import com.kaola.marketing.model.vo.PromotionVO;
import com.kaola.marketing.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 促销接口
 *
 * @author Kaola Team
 */
@Tag(name = "促销接口", description = "促销活动、优惠券等接口")
@RestController
@RequiredArgsConstructor
@Validated
public class PromotionController {

    private final PromotionService promotionService;

    /**
     * 获取促销活动列表
     *
     * @param storeId 门店ID
     * @return 促销活动列表
     */
    @Operation(summary = "获取促销活动", description = "获取指定门店的促销活动列表")
    @GetMapping("/promotion/list")
    public Result<List<PromotionVO>> getPromotionList(
            @Parameter(description = "门店ID")
            @RequestParam(required = false) Long storeId) {
        List<PromotionVO> promotions = promotionService.getActivePromotions(storeId);
        return Result.success(promotions);
    }

    /**
     * 获取可用优惠券
     *
     * @param userId 用户ID
     * @return 优惠券列表
     */
    @Operation(summary = "获取可用优惠券", description = "获取用户可使用的优惠券列表")
    @GetMapping("/coupon/available")
    public Result<List<CouponVO>> getAvailableCoupons(
            @Parameter(description = "用户ID", hidden = true)
            @RequestHeader("X-User-Id") Long userId) {
        List<CouponVO> coupons = promotionService.getAvailableCoupons(userId);
        return Result.success(coupons);
    }

    /**
     * 领取优惠券
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @return 操作结果
     */
    @Operation(summary = "领取优惠券", description = "用户领取指定优惠券")
    @PostMapping("/coupon/receive")
    public Result<Boolean> receiveCoupon(
            @Parameter(description = "用户ID", hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "优惠券ID", required = true)
            @RequestParam @NotNull(message = "优惠券ID不能为空") Long couponId) {
        boolean success = promotionService.receiveCoupon(userId, couponId);
        return Result.success(success);
    }

    /**
     * 使用优惠券
     *
     * @param orderId  订单ID
     * @param couponId 优惠券ID
     * @return 操作结果
     */
    @Operation(summary = "使用优惠券", description = "将优惠券应用到指定订单")
    @PostMapping("/coupon/apply")
    public Result<Boolean> applyCoupon(
            @Parameter(description = "订单ID", required = true)
            @RequestParam @NotNull(message = "订单ID不能为空") Long orderId,
            @Parameter(description = "优惠券ID", required = true)
            @RequestParam @NotNull(message = "优惠券ID不能为空") Long couponId) {
        boolean success = promotionService.applyCoupon(orderId, couponId);
        return Result.success(success);
    }
}
