package com.kaola.product.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 分类VO - 包含项目列表
 */
@Data
public class CategoryVO {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类图标
     */
    private String icon;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 该分类下的项目列表
     */
    private List<ProjectVO> projects;
}
