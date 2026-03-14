package com.kaola.marketing.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.marketing.model.dto.CheckAvailableRequest;
import com.kaola.marketing.model.dto.ValidateCouponRequest;
import com.kaola.marketing.model.dto.ValidateCouponResponse;
import com.kaola.marketing.model.vo.AvailableCouponVO;
import com.kaola.marketing.model.vo.UserCouponVO;
import com.kaola.marketing.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券控制器 - 用户端API
 * 用于小程序端优惠券相关操作
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "优惠券", description = "优惠券相关接口（用于小程序端）")
@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "获取用户优惠券列表", description = "获取用户拥有的优惠券列表")
    @GetMapping("/my-list")
    public Result<List<UserCouponVO>> getUserCouponList(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "状态 (1-未使用 2-已使用 3-已过期, null-全部)") @RequestParam(required = false) Integer status
    ) {
        log.info("API调用: 获取用户优惠券列表, userId: {}, status: {}", userId, status);
        try {
            List<UserCouponVO> list = couponService.getUserCouponList(userId, status);
            return Result.success(list);
        } catch (Exception e) {
            log.error("获取用户优惠券列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "领取优惠券", description = "用户领取优惠券")
    @PostMapping("/{id}/claim")
    public Result<Boolean> claimCoupon(
            @Parameter(description = "优惠券ID") @PathVariable Long id,
            @Parameter(description = "用户ID") @RequestParam Long userId
    ) {
        log.info("API调用: 领取优惠券, couponId: {}, userId: {}", id, userId);
        try {
            boolean success = couponService.claimCoupon(id, userId);
            return Result.success(success);
        } catch (Exception e) {
            log.error("领取优惠券失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "获取订单可用优惠券", description = "获取订单可用的优惠券列表（含优惠金额）")
    @PostMapping("/available")
    public Result<List<AvailableCouponVO>> getAvailableCoupons(
            @RequestBody CheckAvailableRequest request
    ) {
        log.info("API调用: 获取订单可用优惠券, userId: {}, orderAmount: {}",
                request.getUserId(), request.getOrderAmount());
        try {
            List<AvailableCouponVO> list = couponService.getAvailableCoupons(request);
            return Result.success(list);
        } catch (Exception e) {
            log.error("获取订单可用优惠券失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "验证优惠券", description = "验证优惠券是否可用于当前订单")
    @PostMapping("/validate")
    public Result<ValidateCouponResponse> validateCoupon(
            @RequestBody ValidateCouponRequest request
    ) {
        log.info("API调用: 验证优惠券, userCouponId: {}, userId: {}",
                request.getUserCouponId(), request.getUserId());
        try {
            ValidateCouponResponse response = couponService.validateCoupon(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("验证优惠券失败", e);
            return Result.error(e.getMessage());
        }
    }
}
