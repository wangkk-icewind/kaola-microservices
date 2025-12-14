package com.kaola.product.model.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 项目分类VO（简化版）
 */
@Data
public class CategoryVO implements Serializable {
    private Long id;
    private String name;
    private String icon;
    private Integer sort;
}
