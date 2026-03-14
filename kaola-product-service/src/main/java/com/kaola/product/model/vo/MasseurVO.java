package com.kaola.product.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 技师列表VO
 *
 * @author Kaola Team
 */
@Data
public class MasseurVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 技师ID
     */
    private Long id;

    /**
     * 所属门店ID
     */
    private Long storeId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 评价数
     */
    private Integer reviewCount;

    /**
     * 服务数
     */
    private Integer orderCount;

    /**
     * 工作年限
     */
    private Integer workYears;

    /**
     * 标签
     */
    private String tags;

    /**
     * 是否可预约
     */
    private Boolean available;

    /**
     * 擅长症状列表
     */
    private List<String> specialties;

    /**
     * 个人简介
     */
    private String introduction;

    /**
     * 服务数量
     */
    private Integer serviceCount;

    /**
     * 工作经验描述
     */
    private String experience;

    /**
     * 可选服务项目列表
     */
    private List<ServiceItemVO> services;

    /**
     * 服务项目VO
     */
    @Data
    public static class ServiceItemVO implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer duration;
        private Boolean selected;
    }
}
