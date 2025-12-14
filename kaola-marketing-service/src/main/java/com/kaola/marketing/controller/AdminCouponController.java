package com.kaola.marketing.controller;

import com.kaola.common.core.dto.PageVO;
import com.kaola.common.core.dto.Result;
import com.kaola.marketing.model.entity.Coupon;
import com.kaola.marketing.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台 - 优惠券管理接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "管理后台 - 优惠券管理", description = "优惠券的增删改查接口")
@RestController
@RequestMapping("/admin/coupon")
@RequiredArgsConstructor
@Validated
public class AdminCouponController {

    private final CouponService couponService;

    /**
     * 分页查询优惠券列表
     */
    @Operation(summary = "分页查询优惠券列表", description = "支持名称搜索、类型筛选和状态筛选")
    @GetMapping("/list")
    public Result<PageVO<Coupon>> getCouponList(
            @Parameter(description = "当前页", example = "1")
            @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "优惠券名称（模糊查询）")
            @RequestParam(required = false) String name,
            @Parameter(description = "优惠券类型 (1-满减券 2-折扣券 3-代金券)")
            @RequestParam(required = false) Integer type,
            @Parameter(description = "状态 (0-禁用 1-启用)")
            @RequestParam(required = false) Integer status) {

        log.info("分页查询优惠券列表, current: {}, pageSize: {}, name: {}, type: {}, status: {}",
                current, pageSize, name, type, status);

        PageVO<Coupon> pageVO = couponService.getCouponList(current, pageSize, name, type, status);
        return Result.success(pageVO);
    }

    /**
     * 获取优惠券详情
     */
    @Operation(summary = "获取优惠券详情", description = "根据ID获取优惠券详细信息")
    @GetMapping("/detail/{id}")
    public Result<Coupon> getCouponDetail(
            @Parameter(description = "优惠券ID", required = true)
            @PathVariable Long id) {

        log.info("获取优惠券详情, id: {}", id);

        Coupon coupon = couponService.getCouponDetail(id);
        return Result.success(coupon);
    }

    /**
     * 创建优惠券
     */
    @Operation(summary = "创建优惠券", description = "创建新的优惠券")
    @PostMapping("/create")
    public Result<Boolean> createCoupon(
            @Parameter(description = "优惠券信息", required = true)
            @RequestBody Coupon coupon) {

        log.info("创建优惠券, name: {}", coupon.getName());

        boolean success = couponService.createCoupon(coupon);
        return Result.success(success);
    }

    /**
     * 更新优惠券
     */
    @Operation(summary = "更新优惠券", description = "更新优惠券信息")
    @PutMapping("/update")
    public Result<Boolean> updateCoupon(
            @Parameter(description = "优惠券信息", required = true)
            @RequestBody Coupon coupon) {

        log.info("更新优惠券, id: {}, name: {}", coupon.getId(), coupon.getName());

        boolean success = couponService.updateCoupon(coupon);
        return Result.success(success);
    }

    /**
     * 删除优惠券
     */
    @Operation(summary = "删除优惠券", description = "软删除优惠券")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteCoupon(
            @Parameter(description = "优惠券ID", required = true)
            @PathVariable Long id) {

        log.info("删除优惠券, id: {}", id);

        boolean success = couponService.deleteCoupon(id);
        return Result.success(success);
    }

    /**
     * 更新优惠券状态
     */
    @Operation(summary = "更新优惠券状态", description = "启用或禁用优惠券")
    @PutMapping("/updateStatus")
    public Result<Boolean> updateCouponStatus(
            @Parameter(description = "优惠券ID", required = true)
            @RequestParam Long id,
            @Parameter(description = "状态 (0-禁用 1-启用)", required = true)
            @RequestParam Integer status) {

        log.info("更新优惠券状态, id: {}, status: {}", id, status);

        boolean success = couponService.updateCouponStatus(id, status);
        return Result.success(success);
    }
}
