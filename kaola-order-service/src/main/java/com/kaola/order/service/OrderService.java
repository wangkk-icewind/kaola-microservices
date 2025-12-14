package com.kaola.order.service;

import com.kaola.order.model.dto.OrderDTO;
import com.kaola.order.model.vo.OrderVO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 订单服务接口
 *
 * @author Kaola Team
 */
public interface OrderService {

    /**
     * 创建订单
     *
     * @param userId 用户ID
     * @param dto    订单数据
     * @return 订单信息
     */
    OrderVO createOrder(Long userId, OrderDTO dto);

    /**
     * 获取订单列表
     *
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    List<OrderVO> getOrderList(Long userId, Integer status);

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderVO getOrderDetail(Long orderId);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean cancelOrder(Long orderId);

    /**
     * 改约订单
     *
     * @param orderId 订单ID
     * @param newDate 新日期
     * @param newTime 新时间
     * @return 是否成功
     */
    boolean rescheduleOrder(Long orderId, LocalDate newDate, LocalTime newTime);

    /**
     * 到店确认
     *
     * @param orderId 订单ID
     * @param qrCode  二维码
     * @return 是否成功
     */
    boolean confirmArrival(Long orderId, String qrCode);

    /**
     * 完成订单
     *
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean completeOrder(Long orderId);
}
