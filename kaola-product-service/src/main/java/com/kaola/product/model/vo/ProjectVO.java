package com.kaola.product.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 项目VO
 *
 * @author Kaola Team
 */
@Data
public class ProjectVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    private Long id;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目图片
     */
    private String image;

    /**
     * 简介
     */
    private String description;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 现价
     */
    private BigDecimal price;

    /**
     * 时长（分钟）
     */
    private Integer duration;

    /**
     * 加钟单价（元/分钟，不含倍率）
     */
    private BigDecimal extraPricePerMinute;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 是否热门
     */
    private Boolean isHot;

    /**
     * 是否推荐
     */
    private Boolean isRecommend;

    /**
     * 标签
     */
    private String tags;

    /**
     * 技法描述
     */
    private String technique;

    /**
     * 技法/项目介绍图片
     */
    private List<String> introImages;

    /**
     * 项目介绍视频URL
     */
    private String introVideo;

    /**
     * 适宜人群图片（每项含image和label）
     */
    private List<Map<String, String>> suitableImages;

    /**
     * 调理方法文字描述
     */
    private String conditioningMethod;

    /**
     * 调理方法图片
     */
    private List<String> conditioningImages;

    /**
     * 调理流程步骤（每项含image/step/desc）
     */
    private List<Map<String, String>> processSteps;

    /**
     * 禁忌人群文字数组
     */
    private List<String> contraindications;
}
