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
 * 商品实体 (电子礼卡/项目礼卡/实物商品)
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_product")
public class Product extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商品类型 (ELECTRONIC_CARD-电子礼卡, PROJECT_CARD-项目礼卡, PHYSICAL_PRODUCT-实物商品)
     */
    @TableField("type")
    private String type;

    /**
     * 商品名称
     */
    @TableField("name")
    private String name;

    /**
     * 商品价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 原价 (用于显示优惠)
     */
    @TableField("original_price")
    private BigDecimal originalPrice;

    /**
     * 面额 (仅礼卡类型使用)
     */
    @TableField("face_value")
    private BigDecimal faceValue;

    /**
     * 有效天数 (仅礼卡类型使用，-1表示永久有效)
     */
    @TableField("valid_days")
    private Integer validDays;

    /**
     * 库存数量
     */
    @TableField("stock")
    private Integer stock;

    /**
     * 销量
     */
    @TableField("sales_count")
    private Integer salesCount;

    /**
     * 商品描述
     */
    @TableField("description")
    private String description;

    /**
     * 商品规格说明
     */
    @TableField("specifications")
    private String specifications;

    /**
     * 商品图片 (JSON数组格式，存储图片URL) - 数据库存储字段
     */
    @JsonIgnore
    @TableField("images")
    private String images;

    /**
     * 商品图片列表 - 用于前端交互
     */
    @TableField(exist = false)
    @JsonProperty("images")
    private List<String> imageList;

    /**
     * 商品轮播图 (JSON数组格式，存储图片URL) - 数据库存储字段
     */
    @JsonIgnore
    @TableField("carousel_images")
    private String carouselImages;

    /**
     * 商品轮播图列表 - 用于前端交互
     */
    @TableField(exist = false)
    @JsonProperty("carouselImages")
    private List<String> carouselImageList;

    /**
     * 状态 (0-下架 1-上架)
     */
    @TableField("status")
    private Integer status;

    /**
     * 排序值 (数字越小越靠前)
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 是否推荐 (0-否 1-是)
     */
    @TableField("is_recommended")
    private Integer isRecommended;

    /**
     * 关联的项目ID (仅项目礼卡使用，JSON数组格式) - 数据库存储字段
     */
    @JsonIgnore
    @TableField("related_project_ids")
    private String relatedProjectIds;

    /**
     * 关联的项目ID列表 - 用于前端交互
     */
    @TableField(exist = false)
    @JsonProperty("relatedProjectIds")
    private List<Long> relatedProjectIdList;

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

    /**
     * 获取轮播图列表 - 从JSON字符串转换为List
     */
    public List<String> getCarouselImageList() {
        if (carouselImageList != null) {
            return carouselImageList;
        }
        if (carouselImages == null || carouselImages.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(carouselImages, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 设置轮播图列表 - 同时更新数据库存储的JSON字符串
     */
    public void setCarouselImageList(List<String> carouselImageList) {
        this.carouselImageList = carouselImageList;
        if (carouselImageList == null || carouselImageList.isEmpty()) {
            this.carouselImages = null;
        } else {
            try {
                this.carouselImages = objectMapper.writeValueAsString(carouselImageList);
            } catch (JsonProcessingException e) {
                this.carouselImages = null;
            }
        }
    }

    /**
     * 获取关联项目ID列表 - 从JSON字符串转换为List
     */
    public List<Long> getRelatedProjectIdList() {
        if (relatedProjectIdList != null) {
            return relatedProjectIdList;
        }
        if (relatedProjectIds == null || relatedProjectIds.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(relatedProjectIds, new TypeReference<List<Long>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 设置关联项目ID列表 - 同时更新数据库存储的JSON字符串
     */
    public void setRelatedProjectIdList(List<Long> relatedProjectIdList) {
        this.relatedProjectIdList = relatedProjectIdList;
        if (relatedProjectIdList == null || relatedProjectIdList.isEmpty()) {
            this.relatedProjectIds = null;
        } else {
            try {
                this.relatedProjectIds = objectMapper.writeValueAsString(relatedProjectIdList);
            } catch (JsonProcessingException e) {
                this.relatedProjectIds = null;
            }
        }
    }
}
