package com.kaola.masseur.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 技师详情VO
 *
 * @author Kaola Team
 */
@Data
public class MasseurDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 技师ID
     */
    private Long id;

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
     * 个人简介
     */
    private String introduction;

    /**
     * 擅长项目
     */
    private String specialty;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 相册
     */
    private List<String> photos;

    /**
     * 证书
     */
    private List<String> certificates;

    // TODO: Cross-service dependency - StoreVO should be fetched via OpenFeign from kaola-store-service
    // /**
    //  * 所属门店
    //  */
    // private StoreVO store;

    // TODO: Cross-service dependency - ProjectVO should be fetched via OpenFeign from kaola-project-service
    // /**
    //  * 服务项目
    //  */
    // private List<ProjectVO> projects;

    // TODO: Cross-service dependency - ReviewVO should be fetched via OpenFeign from kaola-review-service
    // /**
    //  * 最近评价
    //  */
    // private List<ReviewVO> recentReviews;
}
