package com.kaola.order.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 订单列表VO
 *
 * @author Kaola Team
 */
@Data
public class OrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 订单状态描述
     */
    private String statusText;

    /**
     * 订单类型：SERVICE-服务订单, PRODUCT-商品订单, GIFT_CARD-礼卡订单
     */
    private String orderType;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 预约日期
     */
    private LocalDate appointmentDate;

    /**
     * 预约时间
     */
    private LocalTime appointmentTime;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 项目数量
     */
    private Integer itemCount;

    /**
     * 首个项目图片
     */
    private String firstItemImage;

    /**
     * 首个项目名称
     */
    private String firstItemName;

    /**
     * 订单项列表
     */
    private List<OrderItemVO> items;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 支付截止时间
     */
    private LocalDateTime payDeadline;
}
