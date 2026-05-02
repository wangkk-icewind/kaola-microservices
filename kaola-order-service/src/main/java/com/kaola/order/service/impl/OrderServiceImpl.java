package com.kaola.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.order.model.dto.OrderDTO;
import com.kaola.order.model.dto.OrderItemDTO;
import com.kaola.order.model.entity.Order;
import com.kaola.order.model.entity.OrderItem;
import com.kaola.order.model.enums.OrderStatus;
import com.kaola.order.model.vo.OrderItemVO;
import com.kaola.order.model.vo.OrderVO;
import com.kaola.order.client.StoreServiceClient;
import com.kaola.order.mapper.OrderItemMapper;
import com.kaola.order.mapper.OrderMapper;
import com.kaola.order.service.MasseurCacheService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

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
    private final MasseurCacheService masseurCacheService;
    private final StoreServiceClient storeServiceClient;

    private static final String PRODUCT_SERVICE_URL = "http://localhost:8085";

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

        // 服务订单：提前检查技师时段冲突（防止重复预约同一时段）
        if ("SERVICE".equals(dto.getOrderType())) {
            for (OrderItemDTO itemDTO : dto.getItems()) {
                if ("SERVICE".equals(itemDTO.getItemType()) && itemDTO.getMasseurId() != null) {
                    int conflicts = orderRepository.countMasseurConflicts(
                            itemDTO.getMasseurId(),
                            order.getAppointmentDate(),
                            order.getAppointmentTime()
                    );
                    if (conflicts > 0) {
                        throw new RuntimeException("该技师在 " + order.getAppointmentDate()
                                + " " + order.getAppointmentTime() + " 已被预约，请选择其他时段");
                    }
                }
            }
        }

        orderRepository.insert(order);

        // 处理订单项并计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 用于判断订单类型：根据第一个订单项的类型决定
        String orderType = null;

        for (OrderItemDTO itemDTO : dto.getItems()) {
            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setItemType(itemDTO.getItemType()); // 设置订单项类型

            // 设置订单类型（以第一个订单项为准）
            if (orderType == null) {
                if ("PRODUCT".equals(itemDTO.getItemType()) || "GIFT_CARD".equals(itemDTO.getItemType())) {
                    orderType = "PRODUCT";
                } else {
                    orderType = "SERVICE";
                }
            }

            BigDecimal price;
            Integer duration = 0;

            // 根据订单项类型获取不同的信息
            if ("PRODUCT".equals(itemDTO.getItemType()) || "GIFT_CARD".equals(itemDTO.getItemType())) {
                // 商品订单/礼卡订单：使用 productId
                orderItem.setProductId(itemDTO.getProductId());
                orderItem.setQuantity(itemDTO.getQuantity() != null ? itemDTO.getQuantity() : 1);

                // 获取商品信息
                Map<String, Object> productInfo = getProductInfo(itemDTO.getProductId());
                if (productInfo == null) {
                    throw new RuntimeException("商品不存在: " + itemDTO.getProductId());
                }

                Object priceObj = productInfo.get("price");
                price = priceObj instanceof Number ?
                    new BigDecimal(priceObj.toString()) : BigDecimal.ZERO;

                // 商品订单按数量计算
                orderItem.setPrice(price);
                totalAmount = totalAmount.add(price.multiply(new BigDecimal(orderItem.getQuantity())));

            } else {
                // 服务订单：使用 projectId
                orderItem.setMasseurId(itemDTO.getMasseurId());
                orderItem.setProjectId(itemDTO.getProjectId());

                // 获取项目详情
                Map<String, Object> projectInfo = getProjectInfo(itemDTO.getProjectId());
                if (projectInfo == null) {
                    throw new RuntimeException("项目不存在: " + itemDTO.getProjectId());
                }

                // 获取项目基础价格
                Object priceObj = projectInfo.get("price");
                price = priceObj instanceof Number ?
                    new BigDecimal(priceObj.toString()) : BigDecimal.ZERO;

                // 调用定价服务计算含等级/时段/门店系数的动态价格
                BigDecimal dynamicPrice = calculateDynamicPrice(
                        itemDTO.getProjectId(), itemDTO.getMasseurId(),
                        dto.getStoreId(), dto.getAppointmentDate(), dto.getAppointmentTime());
                if (dynamicPrice != null) {
                    price = dynamicPrice;
                }
                orderItem.setPrice(price);

                Object durationObj = projectInfo.get("duration");
                duration = durationObj instanceof Number ?
                    ((Number) durationObj).intValue() : 0;
                orderItem.setDuration(duration);

                // 处理加钟
                if (itemDTO.getExtraDuration() != null && itemDTO.getExtraDuration() > 0) {
                    orderItem.setExtraDuration(itemDTO.getExtraDuration());
                    // TODO: 加钟费用计算逻辑待完善
                    orderItem.setExtraPrice(BigDecimal.ZERO);
                }

                // 累加总金额
                totalAmount = totalAmount.add(price);
                if (orderItem.getExtraPrice() != null) {
                    totalAmount = totalAmount.add(orderItem.getExtraPrice());
                }
            }

            orderItem.setStatus(1); // 待服务/待发货
            orderItems.add(orderItem);
        }

        // 保存订单项
        if (!orderItems.isEmpty()) {
            for (OrderItem item : orderItems) {
                orderItemMapper.insert(item);
            }
        }

        // 更新订单类型和金额
        if (orderType != null) {
            order.setOrderType(orderType);
            log.info("设置订单类型: orderId={}, orderType={}", order.getId(), orderType);
        }
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount.subtract(order.getDiscountAmount()));
        log.info("更新订单前: orderId={}, orderType={}, totalAmount={}", order.getId(), order.getOrderType(), totalAmount);
        orderRepository.updateById(order);
        log.info("更新订单后，重新查询验证: orderId={}", order.getId());

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
        vo.setPrice(item.getPrice());

        // 根据订单项类型处理不同的信息
        if ("PRODUCT".equals(item.getItemType())) {
            // 商品订单项
            vo.setProductId(item.getProductId());
            vo.setQuantity(item.getQuantity() != null ? item.getQuantity() : 1);

            // 计算小计（价格 × 数量）
            BigDecimal subtotal = item.getPrice().multiply(new BigDecimal(vo.getQuantity()));
            vo.setSubtotal(subtotal);

            // 获取商品信息
            try {
                Map<String, Object> productInfo = getProductInfo(item.getProductId());
                if (productInfo != null) {
                    vo.setProductName((String) productInfo.get("name"));
                    vo.setProductImage((String) productInfo.get("image"));
                }
            } catch (Exception e) {
                log.error("获取商品信息失败, productId: {}", item.getProductId(), e);
            }
        } else {
            // 服务订单项
            vo.setProjectId(item.getProjectId());
            vo.setMasseurId(item.getMasseurId());
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
        }

        return vo;
    }

    /**
     * 调用 product-service 计算含等级/时段/门店系数的动态价格
     * 若调用失败则返回 null，调用方回退到基础价格
     */
    @SuppressWarnings("unchecked")
    private BigDecimal calculateDynamicPrice(Long projectId, Long masseurId, Long storeId,
                                              String appointmentDate, String appointmentTime) {
        try {
            if (projectId == null || appointmentDate == null || appointmentTime == null) {
                return null;
            }
            String appointmentDateTime = appointmentDate + "T" + appointmentTime + ":00";

            Map<String, Object> reqBody = new HashMap<>();
            reqBody.put("projectId", projectId);
            if (masseurId != null) reqBody.put("masseurId", masseurId);
            if (storeId != null) reqBody.put("storeId", storeId);
            reqBody.put("appointmentTime", appointmentDateTime);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(reqBody, headers);

            ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                    PRODUCT_SERVICE_URL + "/admin/price/calculate",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            Map<String, Object> body = resp.getBody();
            if (body != null && Integer.valueOf(0).equals(body.get("code"))) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                if (data != null && data.get("finalPrice") != null) {
                    return new BigDecimal(data.get("finalPrice").toString());
                }
            }
        } catch (Exception e) {
            log.warn("动态价格计算失败，回退到基础价格. projectId={}, masseurId={}", projectId, masseurId, e);
        }
        return null;
    }

    /**
     * 获取项目信息（调用 product-service 真实数据）
     */
    private Map<String, Object> getProjectInfo(Long projectId) {
        try {
            String url = PRODUCT_SERVICE_URL + "/project/" + projectId;
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            Map<String, Object> body = response.getBody();
            if (body != null && Integer.valueOf(0).equals(body.get("code"))) {
                return (Map<String, Object>) body.get("data");
            }
        } catch (Exception e) {
            log.error("调用 product-service 获取项目信息失败, projectId: {}", projectId, e);
        }
        return null;
    }

    /**
     * 获取商品信息
     * TODO: 待product-service API完善后，通过HTTP调用获取真实数据
     */
    private Map<String, Object> getProductInfo(Long productId) {
        // 尝试通过 product-service 获取商品信息
        try {
            String url = PRODUCT_SERVICE_URL + "/product/" + productId;
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> body = response.getBody();
            if (body != null && Integer.valueOf(0).equals(body.get("code"))) {
                return (Map<String, Object>) body.get("data");
            }
        } catch (Exception e) {
            log.error("调用 product-service 获取商品信息失败, productId: {}", productId, e);
        }

        // 无法获取商品信息时抛出异常，禁止静默降级
        throw new RuntimeException("无法获取商品 " + productId + " 的价格信息，请检查 product-service 是否正常");
    }

    /**
     * 获取技师信息（使用缓存服务）
     */
    private Map<String, Object> getMasseurInfo(Long masseurId) {
        // 使用缓存服务获取技师信息（自带Feign调用、Redis缓存、降级策略）
        return masseurCacheService.getMasseurInfo(masseurId);
    }

    /**
     * 获取门店名称（通过 Feign 调用 store-service 获取真实数据）
     */
    private String getStoreName(Long storeId) {
        if (storeId == null) {
            return "考拉推拿连锁店";
        }
        try {
            var result = storeServiceClient.getStoreDetail(storeId);
            if (result != null && result.getCode() == 0 && result.getData() != null) {
                return result.getData().getName();
            }
        } catch (Exception e) {
            log.error("调用 store-service 获取门店 {} 名称失败", storeId, e);
        }
        return "考拉推拿连锁店";
    }
}
