package com.kaola.order.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单项VO
 *
 * @author Kaola Team
 */
@Data
public class OrderItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单项ID
     */
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目图片
     */
    private String projectImage;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 小计
     */
    private BigDecimal subtotal;

    /**
     * 时长（分钟）
     */
    private Integer duration;

    /**
     * 技师ID
     */
    private Long masseurId;

    /**
     * 技师名称
     */
    private String masseurName;

    /**
     * 技师头像
     */
    private String masseurAvatar;

    /**
     * 商品ID（商品订单专用）
     */
    private Long productId;

    /**
     * 商品名称（商品订单专用）
     */
    private String productName;

    /**
     * 商品图片（商品订单专用）
     */
    private String productImage;

    /**
     * 加钟时长（分钟）
     */
    private Integer extraDuration;

    /**
     * 加钟费用
     */
    private BigDecimal extraPrice;
}
