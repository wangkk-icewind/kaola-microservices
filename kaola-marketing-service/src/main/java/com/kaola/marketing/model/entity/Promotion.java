package com.kaola.marketing.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 促销活动实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_promotion")
public class Promotion extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 活动名称
     */
    @TableField("name")
    private String name;

    /**
     * 活动图片URL
     */
    @TableField("image")
    private String image;

    /**
     * 活动描述
     */
    @TableField("description")
    private String description;

    /**
     * 跳转链接
     */
    @TableField("link_url")
    private String linkUrl;

    /**
     * 活动类型 (1-满减 2-折扣 3-买赠 4-秒杀)
     */
    @TableField("type")
    private Integer type;

    /**
     * 门店ID (为空表示全部门店)
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 活动开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 活动规则 (JSON格式，根据type不同有不同的规则结构)
     */
    @TableField("rules")
    private String rules;

    /**
     * 状态 (0-禁用 1-启用)
     */
    @TableField("status")
    private Integer status;
}
