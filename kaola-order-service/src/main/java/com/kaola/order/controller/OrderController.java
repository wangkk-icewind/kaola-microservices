package com.kaola.order.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.order.model.dto.OrderDTO;
import com.kaola.order.model.vo.OrderVO;
import com.kaola.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 订单接口
 *
 * @author Kaola Team
 */
@Tag(name = "订单接口", description = "订单创建、查询、取消、改约等接口")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     *
     * @param userId 用户ID
     * @param dto    订单数据
     * @return 订单信息
     */
    @Operation(summary = "创建订单", description = "创建预约订单")
    @PostMapping("/create")
    public Result<OrderVO> createOrder(
            @Parameter(description = "用户ID", hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody OrderDTO dto) {
        OrderVO order = orderService.createOrder(userId, dto);
        return Result.success(order);
    }

    /**
     * 获取订单列表
     *
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    @Operation(summary = "获取订单列表", description = "获取用户的订单列表，可按状态筛选")
    @GetMapping("/list")
    public Result<List<OrderVO>> getOrderList(
            @Parameter(description = "用户ID", hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "订单状态：0-待付款，1-待服务，2-服务中，3-已完成，4-已取消")
            @RequestParam(required = false) Integer status) {
        List<OrderVO> orders = orderService.getOrderList(userId, status);
        return Result.success(orders);
    }

    /**
     * 获取订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详细信息")
    @GetMapping("/{id}")
    public Result<OrderVO> getOrderDetail(
            @Parameter(description = "订单ID", required = true)
            @PathVariable @NotNull(message = "订单ID不能为空") Long id) {
        OrderVO order = orderService.getOrderDetail(id);
        return Result.success(order);
    }

    /**
     * 取消订单
     *
     * @param id 订单ID
     * @return 操作结果
     */
    @Operation(summary = "取消订单", description = "取消指定订单")
    @PostMapping("/{id}/cancel")
    public Result<Boolean> cancelOrder(
            @Parameter(description = "订单ID", required = true)
            @PathVariable @NotNull(message = "订单ID不能为空") Long id) {
        boolean success = orderService.cancelOrder(id);
        return Result.success(success);
    }

    /**
     * 改约订单
     *
     * @param id      订单ID
     * @param newDate 新日期
     * @param newTime 新时间
     * @return 操作结果
     */
    @Operation(summary = "改约订单", description = "修改订单的预约日期和时间")
    @PostMapping("/{id}/reschedule")
    public Result<Boolean> rescheduleOrder(
            @Parameter(description = "订单ID", required = true)
            @PathVariable @NotNull(message = "订单ID不能为空") Long id,
            @Parameter(description = "新日期", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate newDate,
            @Parameter(description = "新时间", required = true)
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime newTime) {
        boolean success = orderService.rescheduleOrder(id, newDate, newTime);
        return Result.success(success);
    }

    /**
     * 到店确认
     *
     * @param id     订单ID
     * @param qrCode 二维码
     * @return 操作结果
     */
    @Operation(summary = "到店确认", description = "用户到店后扫码确认")
    @PostMapping("/{id}/confirm")
    public Result<Boolean> confirmArrival(
            @Parameter(description = "订单ID", required = true)
            @PathVariable @NotNull(message = "订单ID不能为空") Long id,
            @Parameter(description = "二维码", required = true)
            @RequestParam @NotBlank(message = "二维码不能为空") String qrCode) {
        boolean success = orderService.confirmArrival(id, qrCode);
        return Result.success(success);
    }

    /**
     * 完成订单
     *
     * @param id 订单ID
     * @return 操作结果
     */
    @Operation(summary = "完成订单", description = "标记订单为已完成")
    @PostMapping("/{id}/complete")
    public Result<Boolean> completeOrder(
            @Parameter(description = "订单ID", required = true)
            @PathVariable @NotNull(message = "订单ID不能为空") Long id) {
        boolean success = orderService.completeOrder(id);
        return Result.success(success);
    }
}
