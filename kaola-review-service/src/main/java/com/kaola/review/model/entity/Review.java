package com.kaola.review.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 评价实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_review")
public class Review extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 技师ID
     */
    @TableField("masseur_id")
    private Long masseurId;

    /**
     * 门店ID
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 评分 (1.0-5.0)
     */
    @TableField("rating")
    private BigDecimal rating;

    /**
     * 评价内容
     */
    @TableField("content")
    private String content;

    /**
     * 评价图片 (JSON数组格式，存储图片URL)
     */
    @TableField("images")
    private String images;

    /**
     * 商家回复
     */
    @TableField("reply")
    private String reply;

    /**
     * 状态 (0-隐藏 1-显示)
     */
    @TableField("status")
    private Integer status;
}
