package com.kaola.store.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 门店实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_store")
public class Store extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 门店名称
     */
    @TableField("name")
    private String name;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 详细地址
     */
    @TableField("address")
    private String address;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 区/县
     */
    @TableField("district")
    private String district;

    /**
     * 纬度
     */
    @TableField("latitude")
    private BigDecimal latitude;

    /**
     * 经度
     */
    @TableField("longitude")
    private BigDecimal longitude;

    /**
     * 营业开始时间 (格式: HH:mm)
     */
    @TableField("open_time")
    private String openTime;

    /**
     * 营业结束时间 (格式: HH:mm)
     */
    @TableField("close_time")
    private String closeTime;

    /**
     * 门店设施 (JSON数组格式，如["WiFi","停车场","饮品"])
     */
    @TableField("facilities")
    private String facilities;

    /**
     * 门店图片 (JSON数组格式，存储图片URL)
     */
    @TableField("images")
    private String images;

    /**
     * 状态 (0-停业 1-营业中)
     */
    @TableField("status")
    private Integer status;
}
