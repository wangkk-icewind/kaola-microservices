package com.kaola.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.order.model.dto.OrderDTO;
import com.kaola.order.model.dto.OrderItemDTO;
import com.kaola.order.model.entity.Order;
import com.kaola.order.model.entity.OrderItem;
import com.kaola.order.model.enums.OrderStatus;
import com.kaola.order.model.vo.OrderItemVO;
import com.kaola.order.model.vo.OrderVO;
import com.kaola.order.mapper.OrderItemMapper;
import com.kaola.order.mapper.OrderMapper;
import com.kaola.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderRepository;
    private final OrderItemMapper orderItemMapper;
    private final RestTemplate restTemplate;

    private static final String PRODUCT_SERVICE_URL = "http://localhost:8085";
    private static final String MASSEUR_SERVICE_URL = "http://localhost:8084";

// TODO: 待 store-service 创建后，通过 OpenFeign 获取门店信息
    //     private final StoreRepository storeRepository;

    @Override
    @Transactional
    public OrderVO createOrder(Long userId, OrderDTO dto) {
        log.info("创建订单, userId: {}, dto: {}", userId, dto);

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setStoreId(dto.getStoreId());
        order.setTotalAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayAmount(BigDecimal.ZERO);
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        order.setRemark(dto.getRemark());

        // 设置预约日期和时间（处理null和空字符串）
        if (dto.getAppointmentDate() != null && !dto.getAppointmentDate().trim().isEmpty()) {
            order.setAppointmentDate(LocalDate.parse(dto.getAppointmentDate()));
        } else {
            order.setAppointmentDate(LocalDate.now().plusDays(1));
        }
        if (dto.getAppointmentTime() != null && !dto.getAppointmentTime().trim().isEmpty()) {
            order.setAppointmentTime(LocalTime.parse(dto.getAppointmentTime()));
        } else {
            order.setAppointmentTime(LocalTime.of(10, 0));
        }

        orderRepository.insert(order);

        // 处理订单项并计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDTO itemDTO : dto.getItems()) {
            // 获取项目详情
            Map<String, Object> projectInfo = getProjectInfo(itemDTO.getProjectId());
            if (projectInfo == null) {
                throw new RuntimeException("项目不存在: " + itemDTO.getProjectId());
            }

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setMasseurId(itemDTO.getMasseurId());
            orderItem.setProjectId(itemDTO.getProjectId());

            // 获取项目价格和时长
            Object priceObj = projectInfo.get("price");
            BigDecimal price = priceObj instanceof Number ?
                new BigDecimal(priceObj.toString()) : BigDecimal.ZERO;
            orderItem.setPrice(price);

            Object durationObj = projectInfo.get("duration");
            Integer duration = durationObj instanceof Number ?
                ((Number) durationObj).intValue() : 0;
            orderItem.setDuration(duration);

            // 处理加钟
            if (itemDTO.getExtraDuration() != null && itemDTO.getExtraDuration() > 0) {
                orderItem.setExtraDuration(itemDTO.getExtraDuration());
                // TODO: 加钟费用计算逻辑待完善
                orderItem.setExtraPrice(BigDecimal.ZERO);
            }

            orderItem.setStatus(1); // 待服务
            orderItems.add(orderItem);

            // 累加总金额
            totalAmount = totalAmount.add(price);
            if (orderItem.getExtraPrice() != null) {
                totalAmount = totalAmount.add(orderItem.getExtraPrice());
            }
        }

        // 保存订单项
        if (!orderItems.isEmpty()) {
            for (OrderItem item : orderItems) {
                orderItemMapper.insert(item);
            }
        }

        // 更新订单金额
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount.subtract(order.getDiscountAmount()));
        orderRepository.updateById(order);

        log.info("订单创建成功, orderId: {}, orderNo: {}, totalAmount: {}",
            order.getId(), order.getOrderNo(), totalAmount);

        return getOrderDetail(order.getId());
    }

    @Override
    public List<OrderVO> getOrderList(Long userId, Integer status) {
        log.info("获取订单列表, userId: {}, status: {}", userId, status);

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
            .eq(Order::getUserId, userId)
            .orderByDesc(Order::getCreateTime);

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        List<Order> orders = orderRepository.selectList(wrapper);

        return orders.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        log.info("获取订单详情, orderId: {}", orderId);

        Order order = orderRepository.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        return convertToVO(order);
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long orderId) {
        log.info("取消订单, orderId: {}", orderId);

        Order order = orderRepository.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 只有待支付状态可以取消
        if (!order.getStatus().equals(OrderStatus.PENDING_PAYMENT.getCode())) {
            throw new RuntimeException("当前订单状态不可取消");
        }

        order.setStatus(OrderStatus.CANCELLED.getCode());

        return orderRepository.updateById(order) > 0;
    }

    @Override
    @Transactional
    public boolean rescheduleOrder(Long orderId, LocalDate newDate, LocalTime newTime) {
        log.info("改约订单, orderId: {}, newDate: {}, newTime: {}", orderId, newDate, newTime);

        Order order = orderRepository.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 只有待服务状态可以改约
        if (!order.getStatus().equals(OrderStatus.PAID.getCode())) {
            throw new RuntimeException("当前订单状态不可改约");
        }

        order.setAppointmentDate(newDate);
        order.setAppointmentTime(newTime);

        return orderRepository.updateById(order) > 0;
    }

    @Override
    @Transactional
    public boolean confirmArrival(Long orderId, String qrCode) {
        log.info("到店确认, orderId: {}, qrCode: {}", orderId, qrCode);

        Order order = orderRepository.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证二维码
        if (!order.getOrderNo().equals(qrCode)) {
            throw new RuntimeException("二维码验证失败");
        }

        // 只有已支付状态可以确认到店
        if (!order.getStatus().equals(OrderStatus.PAID.getCode())) {
            throw new RuntimeException("当前订单状态不可确认到店");
        }

        order.setStatus(OrderStatus.IN_SERVICE.getCode());

        return orderRepository.updateById(order) > 0;
    }

    @Override
    @Transactional
    public boolean completeOrder(Long orderId) {
        log.info("完成订单, orderId: {}", orderId);

        Order order = orderRepository.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 只有服务中状态可以完成
        if (!order.getStatus().equals(OrderStatus.IN_SERVICE.getCode())) {
            throw new RuntimeException("当前订单状态不可完成");
        }

        order.setStatus(OrderStatus.COMPLETED.getCode());

        return orderRepository.updateById(order) > 0;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "KL" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        // TODO: 待 store-service 创建后，通过 OpenFeign 获取门店信息
        // 填充门店信息 - 根据storeId获取门店名称
        String storeName = getStoreName(order.getStoreId());
        vo.setStoreName(storeName);

        // 填充订单项信息
        List<OrderItem> orderItems = orderItemMapper.findByOrderId(order.getId());
        if (orderItems != null && !orderItems.isEmpty()) {
            List<OrderItemVO> itemVOs = new ArrayList<>();
            for (OrderItem item : orderItems) {
                OrderItemVO itemVO = buildOrderItemVO(item);
                if (itemVO != null) {
                    itemVOs.add(itemVO);
                }
            }
            vo.setItems(itemVOs);
        }

        return vo;
    }

    /**
     * 构建订单项VO
     */
    private OrderItemVO buildOrderItemVO(OrderItem item) {
        OrderItemVO vo = new OrderItemVO();
        vo.setId(item.getId());
        vo.setProjectId(item.getProjectId());
        vo.setMasseurId(item.getMasseurId());
        vo.setPrice(item.getPrice());
        vo.setDuration(item.getDuration());
        vo.setQuantity(1); // 默认数量为1

        // 计算小计
        BigDecimal subtotal = item.getPrice();
        if (item.getExtraPrice() != null) {
            subtotal = subtotal.add(item.getExtraPrice());
        }
        vo.setSubtotal(subtotal);

        // 获取项目信息
        try {
            Map<String, Object> projectInfo = getProjectInfo(item.getProjectId());
            if (projectInfo != null) {
                vo.setProjectName((String) projectInfo.get("name"));
                vo.setProjectImage((String) projectInfo.get("image"));
            }
        } catch (Exception e) {
            log.error("获取项目信息失败, projectId: {}", item.getProjectId(), e);
        }

        // 获取技师信息
        try {
            Map<String, Object> masseurInfo = getMasseurInfo(item.getMasseurId());
            if (masseurInfo != null) {
                vo.setMasseurName((String) masseurInfo.get("name"));
                vo.setMasseurAvatar((String) masseurInfo.get("avatar"));
            }
        } catch (Exception e) {
            log.error("获取技师信息失败, masseurId: {}", item.getMasseurId(), e);
        }

        return vo;
    }

    /**
     * 获取项目信息
     * TODO: 待product-service创建完成后，通过HTTP调用获取真实数据
     */
    private Map<String, Object> getProjectInfo(Long projectId) {
        // 临时使用模拟数据
        Map<String, Object> projectInfo = new java.util.HashMap<>();
        projectInfo.put("id", projectId);

        // 根据projectId返回不同的项目信息
        switch (projectId.intValue()) {
            case 1:
                projectInfo.put("name", "中式推拿");
                projectInfo.put("price", 198);
                projectInfo.put("duration", 60);
                projectInfo.put("image", "https://example.com/massage1.jpg");
                break;
            case 2:
                projectInfo.put("name", "泰式按摩");
                projectInfo.put("price", 258);
                projectInfo.put("duration", 90);
                projectInfo.put("image", "https://example.com/massage2.jpg");
                break;
            case 3:
                projectInfo.put("name", "足部按摩");
                projectInfo.put("price", 138);
                projectInfo.put("duration", 45);
                projectInfo.put("image", "https://example.com/massage3.jpg");
                break;
            default:
                projectInfo.put("name", "推拿服务");
                projectInfo.put("price", 188);
                projectInfo.put("duration", 60);
                projectInfo.put("image", "https://example.com/massage.jpg");
                break;
        }

        return projectInfo;
    }

    /**
     * 获取技师信息
     * TODO: 待masseur-service API完善后，通过HTTP调用获取真实数据
     */
    private Map<String, Object> getMasseurInfo(Long masseurId) {
        // 临时使用模拟数据
        Map<String, Object> masseurInfo = new java.util.HashMap<>();
        masseurInfo.put("id", masseurId);
        masseurInfo.put("name", "技师" + masseurId + "号");
        masseurInfo.put("avatar", "https://example.com/avatar" + masseurId + ".jpg");
        return masseurInfo;
    }

    /**
     * 获取门店名称
     * TODO: 待store-service创建后，通过HTTP调用获取真实数据
     */
    private String getStoreName(Long storeId) {
        // 临时使用模拟数据
        if (storeId == null) {
            return "考拉推拿连锁店";
        }

        // 根据storeId返回不同的门店名称
        switch (storeId.intValue()) {
            case 1:
                return "考拉推拿·朝阳门店";
            case 2:
                return "考拉推拿·国贸门店";
            case 3:
                return "考拉推拿·三里屯门店";
            case 4:
                return "考拉推拿·西单门店";
            case 5:
                return "考拉推拿·中关村门店";
            default:
                return "考拉推拿连锁店";
        }
    }
}
