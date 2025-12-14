package com.kaola.complaint.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 投诉实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_complaint")
public class Complaint extends BaseEntity {

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
     * 投诉类型 (1-服务态度 2-服务质量 3-虚假宣传 4-收费问题 5-其他)
     */
    @TableField("type")
    private Integer type;

    /**
     * 投诉内容
     */
    @TableField("content")
    private String content;

    /**
     * 投诉图片 (JSON数组格式，存储图片URL)
     */
    @TableField("images")
    private String images;

    /**
     * 状态 (0-待处理 1-处理中 2-已处理 3-已关闭)
     */
    @TableField("status")
    private Integer status;

    /**
     * 处理结果
     */
    @TableField("handle_result")
    private String handleResult;

    /**
     * 处理时间
     */
    @TableField("handle_time")
    private LocalDateTime handleTime;
}
