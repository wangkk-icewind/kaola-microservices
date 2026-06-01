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

    /**
     * 支付订单（模拟支付，待支付→待服务）
     *
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean payOrder(Long orderId);

    /**
     * 重新支付：获取待支付订单的微信调起参数（wxPayParams）。
     * 校验订单归属与待支付状态，复用 payment-service 的待支付记录。
     *
     * @param userId  当前用户ID（鉴权）
     * @param orderId 订单ID
     * @return 含 wxPayParams 的订单详情
     */
    OrderVO getOrderPayParams(Long userId, Long orderId);

    /**
     * 微信支付成功后由 payment-service 回调，通过 orderNo 将订单标记为已支付
     */
    boolean markOrderPaid(String orderNo, String transactionId);

    /**
     * 标记订单为已评价（由 review-service 回调）
     */
    boolean reviewOrder(Long orderId);

    /**
     * 判断用户是否有已完成订单（用于新老客判断）
     */
    boolean hasCompletedOrders(Long userId);

    /**
     * 统计技师"服务过的客户数"（已完成/已评价订单去重客户数）
     */
    int countServedCustomersByMasseur(Long masseurId);
}
