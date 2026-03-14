package com.kaola.order.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_item")
public class OrderItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 订单项类型 (SERVICE-服务项 PRODUCT-商品项)
     */
    @TableField("item_type")
    private String itemType;

    /**
     * 技师ID (服务订单使用)
     */
    @TableField("masseur_id")
    private Long masseurId;

    /**
     * 项目ID (服务订单使用)
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 商品ID (商品订单使用)
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 商品名称 (冗余存储，防止商品删除后无法查看)
     */
    @TableField("product_name")
    private String productName;

    /**
     * 数量 (商品订单使用)
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 服务时长 (分钟)
     */
    @TableField("duration")
    private Integer duration;

    /**
     * 加钟时长 (分钟)
     */
    @TableField("extra_duration")
    private Integer extraDuration;

    /**
     * 加钟费用
     */
    @TableField("extra_price")
    private BigDecimal extraPrice;

    /**
     * 服务开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 服务结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 状态 (0-取消 1-待服务 2-服务中 3-已完成)
     */
    @TableField("status")
    private Integer status;
}
