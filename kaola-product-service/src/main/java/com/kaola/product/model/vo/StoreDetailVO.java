package com.kaola.product.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 门店详情VO
 *
 * @author Kaola Team
 */
@Data
public class StoreDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 门店ID
     */
    private Long id;

    /**
     * 门店名称
     */
    private String name;

    /**
     * 门店图片
     */
    private String image;

    /**
     * 门店相册
     */
    private List<String> photos;

    /**
     * 地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 距离（米）
     */
    private Double distance;

    /**
     * 营业时间
     */
    private String businessHours;

    /**
     * 是否营业中
     */
    private Boolean isOpen;

    /**
     * 门店简介
     */
    private String description;

    /**
     * 技师列表
     */
    private List<MasseurVO> masseurs;

    /**
     * 项目分类
     */
    private List<ProjectCategoryVO> categories;

    /**
     * 热门项目
     */
    private List<ProjectVO> hotProjects;
}
