package com.kaola.product.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

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
}
