package com.kaola.product.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaola.product.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务项目实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_project")
public class Project extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 项目名称
     */
    @TableField("name")
    private String name;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 服务时长 (分钟)
     */
    @TableField("duration")
    private Integer duration;

    /**
     * 基础价格
     */
    @TableField("base_price")
    private BigDecimal basePrice;

    /**
     * 加钟每分钟单价（如果为NULL则自动计算 = base_price / duration）
     */
    @TableField("extra_price_per_minute")
    private BigDecimal extraPricePerMinute;

    /**
     * 项目描述
     */
    @TableField("description")
    private String description;

    /**
     * 项目图片 (JSON数组格式，存储图片URL) - 数据库存储字段
     */
    @JsonIgnore
    @TableField("images")
    private String images;

    /**
     * 项目图片列表 - 用于前端交互
     */
    @TableField(exist = false)
    @JsonProperty("images")
    private List<String> imageList;

    /**
     * 状态 (0-下架 1-上架)
     */
    @TableField("status")
    private Integer status;

    /**
     * 价格 - 用于前端显示 (与basePrice相同)
     */
    @TableField(exist = false)
    private BigDecimal price;

    /**
     * 原价 - 用于前端显示 (与basePrice相同)
     */
    @TableField(exist = false)
    private BigDecimal originalPrice;

    /**
     * 销量 - 用于前端显示
     */
    @TableField(exist = false)
    private Integer salesCount;

    /**
     * 分类名称 - 用于前端显示
     */
    @TableField(exist = false)
    private String categoryName;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取图片列表 - 从JSON字符串转换为List
     */
    public List<String> getImageList() {
        if (imageList != null) {
            return imageList;
        }
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(images, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 设置图片列表 - 同时更新数据库存储的JSON字符串
     */
    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
        if (imageList == null || imageList.isEmpty()) {
            this.images = null;
        } else {
            try {
                this.images = objectMapper.writeValueAsString(imageList);
            } catch (JsonProcessingException e) {
                this.images = null;
            }
        }
    }
}
