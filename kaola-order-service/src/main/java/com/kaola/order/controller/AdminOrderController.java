package com.kaola.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.Result;
import com.kaola.order.model.entity.Order;
import com.kaola.common.model.vo.PageVO;
import com.kaola.order.mapper.OrderMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 管理后台 - 订单管理接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "管理后台 - 订单管理", description = "订单的查询和管理接口")
@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderMapper orderRepository;

    /**
     * 分页查询订单列表
     */
    @Operation(summary = "分页查询订单列表", description = "支持订单号搜索、状态筛选、门店筛选和日期范围筛选")
    @GetMapping("/list")
    public Result<PageVO<Order>> getOrderList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        log.info("分页查询订单列表, current: {}, pageSize: {}, orderNo: {}, status: {}, storeId: {}, startDate: {}, endDate: {}",
                 current, pageSize, orderNo, status, storeId, startDate, endDate);

        Page<Order> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (orderNo != null && !orderNo.trim().isEmpty()) {
            wrapper.like(Order::getOrderNo, orderNo.trim());
        }

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        if (storeId != null) {
            wrapper.eq(Order::getStoreId, storeId);
        }

        if (startDate != null && !startDate.isEmpty()) {
            try {
                wrapper.ge(Order::getAppointmentDate, LocalDate.parse(startDate));
            } catch (Exception e) {
                log.warn("Invalid start date format: {}", startDate);
            }
        }

        if (endDate != null && !endDate.isEmpty()) {
            try {
                wrapper.le(Order::getAppointmentDate, LocalDate.parse(endDate));
            } catch (Exception e) {
                log.warn("Invalid end date format: {}", endDate);
            }
        }

        wrapper.orderByDesc(Order::getCreateTime);

        IPage<Order> pageResult = orderRepository.selectPage(page, wrapper);
        PageVO<Order> pageVO = PageVO.of(pageResult);

        return Result.success(pageVO);
    }

    /**
     * 获取订单详情
     */
    @Operation(summary = "获取订单详情", description = "根据ID获取订单详细信息")
    @GetMapping("/detail/{id}")
    public Result<Order> getOrderDetail(@PathVariable Long id) {
        log.info("获取订单详情, id: {}", id);

        Order order = orderRepository.selectById(id);
        if (order == null || order.getDeleted() == 1) {
            return Result.error("订单不存在");
        }

        return Result.success(order);
    }

    /**
     * 取消订单
     */
    @Operation(summary = "取消订单", description = "取消指定订单")
    @PostMapping("/cancel")
    public Result<Boolean> cancelOrder(@RequestBody Order request) {
        log.info("取消订单, id: {}", request.getId());

        if (request.getId() == null) {
            return Result.error("订单ID不能为空");
        }

        Order order = orderRepository.selectById(request.getId());
        if (order == null || order.getDeleted() == 1) {
            return Result.error("订单不存在");
        }

        // Only allow canceling orders in certain statuses
        if (order.getStatus() != 1 && order.getStatus() != 2) {
            return Result.error("当前状态不允许取消");
        }

        order.setStatus(0); // 0 = canceled
        order.setRemark(request.getRemark() != null ? request.getRemark() : "管理员取消");
        int rows = orderRepository.updateById(order);

        return rows > 0 ? Result.success(true) : Result.error("取消失败");
    }

    /**
     * 完成订单
     */
    @Operation(summary = "完成订单", description = "标记订单为已完成")
    @PostMapping("/complete/{id}")
    public Result<Boolean> completeOrder(@PathVariable Long id) {
        log.info("完成订单, id: {}", id);

        Order order = orderRepository.selectById(id);
        if (order == null || order.getDeleted() == 1) {
            return Result.error("订单不存在");
        }

        if (order.getStatus() != 3) { // 3 = in service
            return Result.error("只能完成服务中的订单");
        }

        order.setStatus(4); // 4 = completed
        int rows = orderRepository.updateById(order);

        return rows > 0 ? Result.success(true) : Result.error("操作失败");
    }

    /**
     * 退款
     */
    @Operation(summary = "订单退款", description = "处理订单退款")
    @PostMapping("/refund")
    public Result<Boolean> refundOrder(@RequestBody Order request) {
        log.info("订单退款, id: {}", request.getId());

        if (request.getId() == null) {
            return Result.error("订单ID不能为空");
        }

        Order order = orderRepository.selectById(request.getId());
        if (order == null || order.getDeleted() == 1) {
            return Result.error("订单不存在");
        }

        // Check if refund is allowed
        if (order.getStatus() == 0 || order.getStatus() == 4 || order.getStatus() == 5) {
            return Result.error("当前状态不允许退款");
        }

        order.setStatus(0); // Set to canceled after refund
        order.setRemark(request.getRemark() != null ? request.getRemark() : "管理员退款");
        int rows = orderRepository.updateById(order);

        return rows > 0 ? Result.success(true) : Result.error("退款失败");
    }
}
