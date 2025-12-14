package com.kaola.product.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 项目分类VO
 *
 * @author Kaola Team
 */
@Data
public class ProjectCategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 父级ID
     */
    private Long parentId;

    /**
     * 项目数量
     */
    private Integer projectCount;

    /**
     * 子分类
     */
    private List<ProjectCategoryVO> children;

    /**
     * 分类下的项目
     */
    private List<ProjectVO> projects;
}
