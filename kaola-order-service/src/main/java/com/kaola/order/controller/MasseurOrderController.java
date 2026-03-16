package com.kaola.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.common.core.dto.Result;
import com.kaola.order.client.ProductServiceClient;
import com.kaola.order.client.StoreServiceClient;
import com.kaola.order.client.UserServiceClient;
import com.kaola.order.mapper.OrderItemMapper;
import com.kaola.order.mapper.OrderMapper;
import com.kaola.order.model.entity.Order;
import com.kaola.order.model.entity.OrderItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 技师端 - 订单接口
 */
@Slf4j
@Tag(name = "技师端 - 订单", description = "技师查看和操作订单的接口")
@RestController
@RequestMapping("/masseur/orders")
@RequiredArgsConstructor
public class MasseurOrderController {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final UserServiceClient userServiceClient;
    private final StoreServiceClient storeServiceClient;
    private final ProductServiceClient productServiceClient;

    /** 技师端订单 VO */
    @Data
    public static class MasseurOrderVO {
        private Long id;
        private String orderNo;
        private String customerName;
        private String customerPhone;
        private String serviceName;
        private String appointmentTime;
        private String storeName;
        private String storeAddress;
        private Integer status;
        private String statusText;
        private BigDecimal amount;
        private String remark;
        private Integer duration;
    }

    private static final Map<Integer, String> STATUS_TEXT = Map.of(
        0, "已取消", 1, "待支付", 2, "待服务", 3, "服务中", 4, "已完成", 5, "已评价"
    );

    /** 获取技师的订单列表 */
    @Operation(summary = "技师订单列表")
    @GetMapping
    public Result<List<MasseurOrderVO>> getOrderList(
            @RequestHeader(value = "X-Masseur-Id", required = false) Long masseurId,
            @RequestParam(required = false) Integer status) {

        if (masseurId == null) return Result.error("未登录");
        log.info("技师订单列表, masseurId: {}, status: {}", masseurId, status);

        // 通过 order_item 找到该技师的所有订单ID
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getMasseurId, masseurId).eq(OrderItem::getItemType, "SERVICE");
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
        if (items.isEmpty()) return Result.success(Collections.emptyList());

        Set<Long> orderIds = items.stream().map(OrderItem::getOrderId).collect(Collectors.toSet());
        Map<Long, OrderItem> itemByOrderId = new HashMap<>();
        items.forEach(i -> itemByOrderId.putIfAbsent(i.getOrderId(), i));

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Order::getId, orderIds).eq(Order::getDeleted, 0);
        if (status != null) wrapper.eq(Order::getStatus, status);
        wrapper.orderByDesc(Order::getCreateTime);

        List<Order> orders = orderMapper.selectList(wrapper);
        return Result.success(buildVOList(orders, itemByOrderId));
    }

    /** 今日统计 */
    @Operation(summary = "今日统计")
    @GetMapping("/today-stats")
    public Result<Map<String, Object>> getTodayStats(
            @RequestHeader(value = "X-Masseur-Id", required = false) Long masseurId) {

        if (masseurId == null) return Result.error("未登录");
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getMasseurId, masseurId).eq(OrderItem::getItemType, "SERVICE");
        List<Long> myOrderIds = orderItemMapper.selectList(itemWrapper)
                .stream().map(OrderItem::getOrderId).collect(Collectors.toList());

        long orderCount = 0, completedCount = 0;
        BigDecimal earnings = BigDecimal.ZERO;

        if (!myOrderIds.isEmpty()) {
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Order::getId, myOrderIds)
                   .eq(Order::getAppointmentDate, today)
                   .eq(Order::getDeleted, 0);
            List<Order> todayOrders = orderMapper.selectList(wrapper);
            orderCount = todayOrders.size();
            for (Order o : todayOrders) {
                if (o.getStatus() != null && (o.getStatus() == 4 || o.getStatus() == 5)) {
                    completedCount++;
                    earnings = earnings.add(o.getPayAmount() != null ? o.getPayAmount() : BigDecimal.ZERO);
                }
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("orderCount", orderCount);
        stats.put("completedCount", completedCount);
        stats.put("earnings", earnings);
        return Result.success(stats);
    }

    /** 即将到来的订单 */
    @Operation(summary = "即将到来的订单")
    @GetMapping("/upcoming")
    public Result<List<MasseurOrderVO>> getUpcoming(
            @RequestHeader(value = "X-Masseur-Id", required = false) Long masseurId) {

        if (masseurId == null) return Result.error("未登录");

        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getMasseurId, masseurId).eq(OrderItem::getItemType, "SERVICE");
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
        if (items.isEmpty()) return Result.success(Collections.emptyList());

        Set<Long> orderIds = items.stream().map(OrderItem::getOrderId).collect(Collectors.toSet());
        Map<Long, OrderItem> itemByOrderId = new HashMap<>();
        items.forEach(i -> itemByOrderId.putIfAbsent(i.getOrderId(), i));

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Order::getId, orderIds)
               .ge(Order::getAppointmentDate, LocalDate.now())
               .in(Order::getStatus, Arrays.asList(1, 2, 3))
               .eq(Order::getDeleted, 0)
               .orderByAsc(Order::getAppointmentDate, Order::getAppointmentTime)
               .last("LIMIT 5");

        List<Order> orders = orderMapper.selectList(wrapper);
        return Result.success(buildVOList(orders, itemByOrderId));
    }

    /** 订单详情 */
    @Operation(summary = "技师订单详情")
    @GetMapping("/{id}")
    public Result<MasseurOrderVO> getOrderDetail(
            @RequestHeader(value = "X-Masseur-Id", required = false) Long masseurId,
            @PathVariable Long id) {

        if (masseurId == null) return Result.error("未登录");
        Order order = orderMapper.selectById(id);
        if (order == null || order.getDeleted() == 1) return Result.error("订单不存在");

        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId, id).eq(OrderItem::getItemType, "SERVICE");
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
        OrderItem item = items.isEmpty() ? null : items.get(0);
        Map<Long, OrderItem> itemByOrderId = new HashMap<>();
        if (item != null) itemByOrderId.put(id, item);

        List<MasseurOrderVO> vos = buildVOList(Collections.singletonList(order), itemByOrderId);
        return vos.isEmpty() ? Result.error("订单不存在") : Result.success(vos.get(0));
    }

    /** 确认订单（待支付→待服务） */
    @Operation(summary = "确认订单")
    @PutMapping("/{id}/confirm")
    public Result<Boolean> confirmOrder(
            @RequestHeader(value = "X-Masseur-Id", required = false) Long masseurId,
            @PathVariable Long id) {
        return updateStatus(masseurId, id, 1, 2);
    }

    /** 开始服务（待服务→服务中） */
    @Operation(summary = "开始服务")
    @PutMapping("/{id}/start")
    public Result<Boolean> startService(
            @RequestHeader(value = "X-Masseur-Id", required = false) Long masseurId,
            @PathVariable Long id) {
        return updateStatus(masseurId, id, 2, 3);
    }

    /** 完成服务（服务中→已完成） */
    @Operation(summary = "完成服务")
    @PutMapping("/{id}/complete")
    public Result<Boolean> completeService(
            @RequestHeader(value = "X-Masseur-Id", required = false) Long masseurId,
            @PathVariable Long id) {
        return updateStatus(masseurId, id, 3, 4);
    }

    // ===== 私有方法 =====

    private Result<Boolean> updateStatus(Long masseurId, Long id, int fromStatus, int toStatus) {
        if (masseurId == null) return Result.error("未登录");
        Order order = orderMapper.selectById(id);
        if (order == null || order.getDeleted() == 1) return Result.error("订单不存在");
        if (order.getStatus() != fromStatus) return Result.error("当前状态不允许此操作");
        order.setStatus(toStatus);
        int rows = orderMapper.updateById(order);
        return rows > 0 ? Result.success(true) : Result.error("操作失败");
    }

    private List<MasseurOrderVO> buildVOList(List<Order> orders, Map<Long, OrderItem> itemByOrderId) {
        // 批量查名称
        Set<Long> storeIds = orders.stream().map(Order::getStoreId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> userIds = orders.stream().map(Order::getUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> projectIds = itemByOrderId.values().stream()
                .map(OrderItem::getProjectId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, String> storeNames = new HashMap<>();
        for (Long sid : storeIds) {
            try {
                Result<StoreServiceClient.StoreVO> r = storeServiceClient.getStoreDetail(sid);
                if (r != null && r.getData() != null) storeNames.put(sid, r.getData().getName());
            } catch (Exception e) { log.warn("获取门店失败 {}", sid); }
        }

        Map<Long, UserServiceClient.UserVO> userMap = new HashMap<>();
        for (Long uid : userIds) {
            try {
                Result<UserServiceClient.UserVO> r = userServiceClient.getUserInfo(uid);
                if (r != null && r.getData() != null) userMap.put(uid, r.getData());
            } catch (Exception e) { log.warn("获取用户失败 {}", uid); }
        }

        Map<Long, String> projectNames = new HashMap<>();
        for (Long pid : projectIds) {
            try {
                Result<ProductServiceClient.ProjectVO> r = productServiceClient.getProjectDetail(pid);
                if (r != null && r.getData() != null) projectNames.put(pid, r.getData().getName());
            } catch (Exception e) { log.warn("获取项目失败 {}", pid); }
        }

        return orders.stream().map(order -> {
            MasseurOrderVO vo = new MasseurOrderVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setStatus(order.getStatus());
            vo.setStatusText(STATUS_TEXT.getOrDefault(order.getStatus() != null ? order.getStatus() : -1, "未知"));
            vo.setAmount(order.getPayAmount());
            vo.setRemark(order.getRemark());

            // 预约时间
            if (order.getAppointmentDate() != null && order.getAppointmentTime() != null) {
                String time = order.getAppointmentTime().toString();
                // 截成 HH:mm
                if (time.length() > 5) time = time.substring(0, 5);
                vo.setAppointmentTime(order.getAppointmentDate() + " " + time);
            }

            // 客户信息（手机号脱敏）
            UserServiceClient.UserVO uvo = userMap.get(order.getUserId());
            if (uvo != null) {
                vo.setCustomerName(uvo.getNickname() != null ? uvo.getNickname() : "用户");
                String phone = uvo.getPhone();
                if (phone != null && phone.length() >= 7) {
                    phone = phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
                }
                vo.setCustomerPhone(phone != null ? phone : "");
            } else {
                vo.setCustomerName("用户");
                vo.setCustomerPhone("");
            }

            // 门店信息
            vo.setStoreName(storeNames.getOrDefault(order.getStoreId(), ""));

            // 项目信息
            OrderItem item = itemByOrderId.get(order.getId());
            if (item != null) {
                vo.setServiceName(projectNames.getOrDefault(item.getProjectId(), ""));
                vo.setDuration(item.getDuration());
            }

            return vo;
        }).collect(Collectors.toList());
    }
}
