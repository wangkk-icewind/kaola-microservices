package com.kaola.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.Result;
import com.kaola.order.client.MasseurServiceClient;
import com.kaola.order.client.ProductServiceClient;
import com.kaola.order.client.StoreServiceClient;
import com.kaola.order.client.UserServiceClient;
import com.kaola.order.mapper.OrderItemMapper;
import com.kaola.order.mapper.OrderMapper;
import com.kaola.order.model.entity.Order;
import com.kaola.order.model.entity.OrderItem;
import com.kaola.common.model.vo.PageVO;
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
    private final OrderItemMapper orderItemMapper;
    private final UserServiceClient userServiceClient;
    private final StoreServiceClient storeServiceClient;
    private final MasseurServiceClient masseurServiceClient;
    private final ProductServiceClient productServiceClient;
    private final com.kaola.order.service.OrderService orderService;
    private final com.kaola.order.mapper.RefundMapper refundMapper;

    /**
     * 管理端订单列表 VO
     */
    @Data
    public static class AdminOrderVO {
        private Long id;
        private String orderNo;
        private Long userId;
        private String userName;
        private String userPhone;
        private Long storeId;
        private String storeName;
        private Long masseurId;
        private String masseurName;
        private Long projectId;
        private String projectName;
        /** 预约时间（合并 appointmentDate + appointmentTime，格式：yyyy-MM-ddTHH:mm:ss） */
        private String appointmentTime;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private BigDecimal payAmount;
        private Integer duration;
        private Integer payType;
        private Integer status;
        private String remark;
        private String createTime;
        private String updateTime;
    }

    /**
     * 分页查询订单列表（含客户/门店/技师/项目名称）
     */
    @Operation(summary = "分页查询订单列表", description = "支持订单号搜索、状态筛选、门店筛选和日期范围筛选")
    @GetMapping("/list")
    public Result<PageVO<AdminOrderVO>> getOrderList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        log.info("分页查询订单列表, current: {}, pageSize: {}, orderNo: {}, status: {}, storeId: {}",
                 current, pageSize, orderNo, status, storeId);

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
            try { wrapper.ge(Order::getAppointmentDate, LocalDate.parse(startDate)); }
            catch (Exception e) { log.warn("Invalid startDate: {}", startDate); }
        }
        if (endDate != null && !endDate.isEmpty()) {
            try { wrapper.le(Order::getAppointmentDate, LocalDate.parse(endDate)); }
            catch (Exception e) { log.warn("Invalid endDate: {}", endDate); }
        }
        wrapper.orderByDesc(Order::getCreateTime);

        IPage<Order> pageResult = orderRepository.selectPage(page, wrapper);
        List<Order> orders = pageResult.getRecords();

        // 批量查询订单项（取每个订单的第一个服务项）
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());
        Map<Long, OrderItem> firstItemMap = new HashMap<>();
        if (!orderIds.isEmpty()) {
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.in(OrderItem::getOrderId, orderIds)
                       .eq(OrderItem::getItemType, "SERVICE");
            List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
            // 每个订单只保留第一个
            for (OrderItem item : items) {
                firstItemMap.putIfAbsent(item.getOrderId(), item);
            }
        }

        // 收集去重 ID
        Set<Long> storeIds = orders.stream().map(Order::getStoreId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> userIds  = orders.stream().map(Order::getUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> masseurIds  = new HashSet<>();
        Set<Long> projectIds  = new HashSet<>();
        firstItemMap.values().forEach(item -> {
            if (item.getMasseurId() != null) masseurIds.add(item.getMasseurId());
            if (item.getProjectId() != null) projectIds.add(item.getProjectId());
        });

        // 缓存名称映射（每个 userId 只查询一次）
        Map<Long, UserServiceClient.UserVO> userVOMap   = buildUserVOMap(userIds);
        Map<Long, String> storeNameMap   = buildStoreNameMap(storeIds);
        Map<Long, String> masseurNameMap = buildMasseurNameMap(masseurIds);
        Map<Long, String> projectNameMap = buildProjectNameMap(projectIds);

        // 组装 VO
        List<AdminOrderVO> voList = orders.stream().map(order -> {
            AdminOrderVO vo = new AdminOrderVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setUserId(order.getUserId());
            UserServiceClient.UserVO uvo = userVOMap.get(order.getUserId());
            vo.setUserName(uvo != null && uvo.getNickname() != null ? uvo.getNickname() : "");
            vo.setUserPhone(uvo != null && uvo.getPhone() != null ? uvo.getPhone() : "");
            vo.setStoreId(order.getStoreId());
            vo.setStoreName(storeNameMap.getOrDefault(order.getStoreId(), ""));
            vo.setTotalAmount(order.getTotalAmount());
            vo.setDiscountAmount(order.getDiscountAmount());
            vo.setPayAmount(order.getPayAmount());
            vo.setPayType(order.getPayMethod());
            vo.setStatus(order.getStatus());
            vo.setRemark(order.getRemark());
            vo.setCreateTime(order.getCreateTime() != null ? order.getCreateTime().toString() : null);
            vo.setUpdateTime(order.getUpdateTime() != null ? order.getUpdateTime().toString() : null);

            // 合并预约日期+时间
            if (order.getAppointmentDate() != null && order.getAppointmentTime() != null) {
                vo.setAppointmentTime(order.getAppointmentDate() + "T" + order.getAppointmentTime());
            } else if (order.getAppointmentDate() != null) {
                vo.setAppointmentTime(order.getAppointmentDate() + "T00:00:00");
            }

            // 技师/项目信息
            OrderItem item = firstItemMap.get(order.getId());
            if (item != null) {
                vo.setMasseurId(item.getMasseurId());
                vo.setMasseurName(masseurNameMap.getOrDefault(item.getMasseurId(), ""));
                vo.setProjectId(item.getProjectId());
                vo.setProjectName(projectNameMap.getOrDefault(item.getProjectId(), ""));
                int dur = 0;
                if (item.getDuration() != null) dur += item.getDuration();
                if (item.getExtraDuration() != null) dur += item.getExtraDuration();
                vo.setDuration(dur > 0 ? dur : null);
            }
            return vo;
        }).collect(Collectors.toList());

        PageVO<AdminOrderVO> pageVO = new PageVO<>();
        pageVO.setRecords(voList);
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setCurrent(pageResult.getCurrent());
        pageVO.setSize(pageResult.getSize());
        pageVO.setPages(pageResult.getPages());

        return Result.success(pageVO);
    }

    /**
     * 获取订单详情（含客户/门店/技师/项目名称）
     */
    @Operation(summary = "获取订单详情", description = "根据ID获取订单详细信息")
    @GetMapping("/detail/{id}")
    public Result<AdminOrderVO> getOrderDetail(@PathVariable Long id) {
        log.info("获取订单详情, id: {}", id);
        Order order = orderRepository.selectById(id);
        if (order == null || order.getDeleted() == 1) {
            return Result.error("订单不存在");
        }

        // 查询订单项
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId, id).eq(OrderItem::getItemType, "SERVICE");
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
        OrderItem item = items.isEmpty() ? null : items.get(0);

        // 查询关联信息
        String storeName = "";
        String userName = "";
        String userPhone = "";
        String masseurName = "";
        String projectName = "";
        Long masseurId = null;
        Long projectId = null;

        if (order.getStoreId() != null) {
            try {
                Result<StoreServiceClient.StoreVO> r = storeServiceClient.getStoreDetail(order.getStoreId());
                if (r != null && r.getData() != null) storeName = r.getData().getName();
            } catch (Exception e) { log.warn("获取门店信息失败, storeId: {}", order.getStoreId()); }
        }
        if (order.getUserId() != null) {
            try {
                Result<UserServiceClient.UserVO> r = userServiceClient.getUserInfo(order.getUserId());
                if (r != null && r.getData() != null) {
                    UserServiceClient.UserVO uvo = r.getData();
                    userName = uvo.getNickname() != null ? uvo.getNickname() : "";
                    userPhone = uvo.getPhone() != null ? uvo.getPhone() : "";
                }
            } catch (Exception e) { log.warn("获取用户信息失败, userId: {}", order.getUserId()); }
        }
        if (item != null) {
            masseurId = item.getMasseurId();
            projectId = item.getProjectId();
            if (masseurId != null) {
                try {
                    Result<MasseurServiceClient.MasseurVO> r = masseurServiceClient.getMasseurDetail(masseurId);
                    if (r != null && r.getData() != null) masseurName = r.getData().getName();
                } catch (Exception e) { log.warn("获取技师信息失败, masseurId: {}", masseurId); }
            }
            if (projectId != null) {
                try {
                    Result<ProductServiceClient.ProjectVO> r = productServiceClient.getProjectDetail(projectId);
                    if (r != null && r.getData() != null) projectName = r.getData().getName();
                } catch (Exception e) { log.warn("获取项目信息失败, projectId: {}", projectId); }
            }
        }

        AdminOrderVO vo = new AdminOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setUserName(userName);
        vo.setUserPhone(userPhone);
        vo.setStoreId(order.getStoreId());
        vo.setStoreName(storeName);
        vo.setMasseurId(masseurId);
        vo.setMasseurName(masseurName);
        vo.setProjectId(projectId);
        vo.setProjectName(projectName);
        vo.setTotalAmount(order.getTotalAmount());
        vo.setDiscountAmount(order.getDiscountAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setPayType(order.getPayMethod());
        vo.setStatus(order.getStatus());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime() != null ? order.getCreateTime().toString() : null);
        vo.setUpdateTime(order.getUpdateTime() != null ? order.getUpdateTime().toString() : null);
        if (order.getAppointmentDate() != null && order.getAppointmentTime() != null) {
            vo.setAppointmentTime(order.getAppointmentDate() + "T" + order.getAppointmentTime());
        } else if (order.getAppointmentDate() != null) {
            vo.setAppointmentTime(order.getAppointmentDate() + "T00:00:00");
        }
        if (item != null) {
            int dur = 0;
            if (item.getDuration() != null) dur += item.getDuration();
            if (item.getExtraDuration() != null) dur += item.getExtraDuration();
            vo.setDuration(dur > 0 ? dur : null);
        }

        return Result.success(vo);
    }

    /**
     * 取消订单
     */
    @Operation(summary = "取消订单", description = "取消指定订单")
    @PostMapping("/cancel")
    public Result<Boolean> cancelOrder(@RequestBody Order request) {
        log.info("取消订单, id: {}", request.getId());
        if (request.getId() == null) return Result.error("订单ID不能为空");

        Order order = orderRepository.selectById(request.getId());
        if (order == null || order.getDeleted() == 1) return Result.error("订单不存在");
        if (order.getStatus() != 1 && order.getStatus() != 2) return Result.error("当前状态不允许取消");

        order.setStatus(0);
        order.setRemark(request.getRemark() != null ? request.getRemark() : "管理员取消");
        int rows = orderRepository.updateById(order);
        return rows > 0 ? Result.success(true) : Result.error("取消失败");
    }

    /**
     * 开始服务（待服务→服务中）
     */
    @Operation(summary = "开始服务", description = "将订单状态从待服务改为服务中")
    @PostMapping("/start/{id}")
    public Result<Boolean> startService(@PathVariable Long id) {
        log.info("开始服务, id: {}", id);
        Order order = orderRepository.selectById(id);
        if (order == null || order.getDeleted() == 1) return Result.error("订单不存在");
        if (order.getStatus() != 2) return Result.error("只能开始待服务状态的订单");

        order.setStatus(3);
        int rows = orderRepository.updateById(order);
        return rows > 0 ? Result.success(true) : Result.error("操作失败");
    }

    /**
     * 完成订单
     */
    @Operation(summary = "完成订单", description = "标记订单为已完成")
    @PostMapping("/complete/{id}")
    public Result<Boolean> completeOrder(@PathVariable Long id) {
        log.info("完成订单, id: {}", id);
        Order order = orderRepository.selectById(id);
        if (order == null || order.getDeleted() == 1) return Result.error("订单不存在");
        if (order.getStatus() != 3) return Result.error("只能完成服务中的订单");

        order.setStatus(4);
        int rows = orderRepository.updateById(order);
        return rows > 0 ? Result.success(true) : Result.error("操作失败");
    }

    /**
     * 退款
     */
    @Operation(summary = "订单退款", description = "后台直接退款：已付款单真退微信 + 状态更新")
    @PostMapping("/refund")
    public Result<Boolean> refundOrder(@RequestBody Order request) {
        log.info("后台直接退款, id: {}", request.getId());
        if (request.getId() == null) return Result.error("订单ID不能为空");
        try {
            boolean ok = orderService.directRefund(request.getId(), request.getRemark(), request.getCommissionOverride());
            return ok ? Result.success(true) : Result.error("退款失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /** 退款记录列表（不传 status=全部；传则按状态筛选，0待审核/1已退款/2已拒绝） */
    @Operation(summary = "退款记录列表", description = "查看退款申请/记录，不传 status 返回全部")
    @GetMapping("/refund/list")
    public Result<java.util.List<java.util.Map<String, Object>>> refundList(
            @RequestParam(required = false) Integer status) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.kaola.order.model.entity.Refund> w =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (status != null) w.eq(com.kaola.order.model.entity.Refund::getStatus, status);
        w.orderByDesc(com.kaola.order.model.entity.Refund::getCreateTime);
        java.util.List<com.kaola.order.model.entity.Refund> list = refundMapper.selectList(w);
        java.util.List<java.util.Map<String, Object>> out = new java.util.ArrayList<>();
        for (com.kaola.order.model.entity.Refund r : list) {
            java.util.Map<String, Object> m = new java.util.HashMap<>();
            m.put("id", r.getId());
            m.put("orderId", r.getOrderId());
            m.put("orderNo", r.getOrderNo());
            m.put("userId", r.getUserId());
            m.put("amount", r.getAmount());
            m.put("reason", r.getReason());
            m.put("status", r.getStatus());
            m.put("auditRemark", r.getAuditRemark());
            m.put("createTime", r.getCreateTime() != null ? r.getCreateTime().toString() : null);
            out.add(m);
        }
        return Result.success(out);
    }

    @Operation(summary = "同意退款", description = "审核通过：真退微信 + 订单置已退款")
    @PostMapping("/refund/approve")
    public Result<Boolean> approveRefund(@RequestBody java.util.Map<String, Object> body) {
        Object id = body.get("refundId");
        if (id == null) return Result.error("refundId 不能为空");
        try {
            return Result.success(orderService.approveRefund(Long.valueOf(id.toString())));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "拒绝退款", description = "审核拒绝：订单状态不变")
    @PostMapping("/refund/reject")
    public Result<Boolean> rejectRefund(@RequestBody java.util.Map<String, Object> body) {
        Object id = body.get("refundId");
        if (id == null) return Result.error("refundId 不能为空");
        String remark = body.get("remark") != null ? body.get("remark").toString() : null;
        try {
            return Result.success(orderService.rejectRefund(Long.valueOf(id.toString()), remark));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // ===== 私有辅助方法 =====

    private Map<Long, String> buildStoreNameMap(Set<Long> ids) {
        Map<Long, String> map = new HashMap<>();
        for (Long id : ids) {
            try {
                Result<StoreServiceClient.StoreVO> r = storeServiceClient.getStoreDetail(id);
                if (r != null && r.getData() != null) map.put(id, r.getData().getName());
            } catch (Exception e) {
                log.warn("获取门店信息失败, storeId: {}", id);
            }
        }
        return map;
    }

    private Map<Long, UserServiceClient.UserVO> buildUserVOMap(Set<Long> ids) {
        Map<Long, UserServiceClient.UserVO> map = new HashMap<>();
        for (Long id : ids) {
            try {
                Result<UserServiceClient.UserVO> r = userServiceClient.getUserInfo(id);
                if (r != null && r.getData() != null) map.put(id, r.getData());
            } catch (Exception e) {
                log.warn("获取用户信息失败, userId: {}", id);
            }
        }
        return map;
    }

    private Map<Long, String> buildMasseurNameMap(Set<Long> ids) {
        Map<Long, String> map = new HashMap<>();
        for (Long id : ids) {
            try {
                Result<MasseurServiceClient.MasseurVO> r = masseurServiceClient.getMasseurDetail(id);
                if (r != null && r.getData() != null) map.put(id, r.getData().getName());
            } catch (Exception e) {
                log.warn("获取技师信息失败, masseurId: {}", id);
            }
        }
        return map;
    }

    private Map<Long, String> buildProjectNameMap(Set<Long> ids) {
        Map<Long, String> map = new HashMap<>();
        for (Long id : ids) {
            try {
                Result<ProductServiceClient.ProjectVO> r = productServiceClient.getProjectDetail(id);
                if (r != null && r.getData() != null) map.put(id, r.getData().getName());
            } catch (Exception e) {
                log.warn("获取项目信息失败, projectId: {}", id);
            }
        }
        return map;
    }
}
