package com.kaola.masseur.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 技师分类排序实体
 */
@Data
@TableName("t_masseur_category_sort")
public class MasseurCategorySort {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("store_id")
    private Long storeId;

    @TableField("category_id")
    private Long categoryId;

    @TableField("masseur_id")
    private Long masseurId;

    @TableField("sort_order")
    private Integer sortOrder;
}
