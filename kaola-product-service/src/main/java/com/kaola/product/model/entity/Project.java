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
import java.util.Map;

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
     * 原价（划线价）- 数据库存储字段
     */
    @TableField("original_price")
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

    /**
     * 技法描述 - 数据库存储字段（直接TEXT，无需JSON转换）
     */
    @TableField("technique")
    private String technique;

    /**
     * 技法/项目介绍图片 - 数据库存储字段（JSON字符串）
     */
    @JsonIgnore
    @TableField("intro_images")
    private String introImagesJson;

    /**
     * 技法/项目介绍图片列表 - 用于前端交互
     */
    @TableField(exist = false)
    @JsonProperty("introImages")
    private List<String> introImages;

    /**
     * 项目介绍视频URL
     */
    @TableField("intro_video")
    private String introVideo;

    /**
     * 适宜人群图片 - 数据库存储字段（JSON字符串）
     */
    @JsonIgnore
    @TableField("suitable_images")
    private String suitableImagesJson;

    /**
     * 适宜人群图片列表 - 用于前端交互
     */
    @TableField(exist = false)
    @JsonProperty("suitableImages")
    private List<Map<String, String>> suitableImages;

    /**
     * 调理方法文字描述
     */
    @TableField("conditioning_method")
    private String conditioningMethod;

    /**
     * 调理方法图片 - 数据库存储字段（JSON字符串）
     */
    @JsonIgnore
    @TableField("conditioning_images")
    private String conditioningImagesJson;

    /**
     * 调理方法图片列表 - 用于前端交互
     */
    @TableField(exist = false)
    @JsonProperty("conditioningImages")
    private List<String> conditioningImages;

    /**
     * 调理流程步骤 - 数据库存储字段（JSON字符串）
     */
    @JsonIgnore
    @TableField("process_steps")
    private String processStepsJson;

    /**
     * 调理流程步骤列表 - 用于前端交互
     */
    @TableField(exist = false)
    @JsonProperty("processSteps")
    private List<Map<String, String>> processSteps;

    /**
     * 禁忌人群 - 数据库存储字段（JSON字符串）
     */
    @JsonIgnore
    @TableField("contraindications")
    private String contraindicationsJson;

    /**
     * 禁忌人群列表 - 用于前端交互
     */
    @TableField(exist = false)
    @JsonProperty("contraindications")
    private List<String> contraindications;

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

    // ===== introImages =====

    public List<String> getIntroImages() {
        if (introImages != null) {
            return introImages;
        }
        if (introImagesJson == null || introImagesJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(introImagesJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    public void setIntroImages(List<String> introImages) {
        this.introImages = introImages;
        if (introImages == null || introImages.isEmpty()) {
            this.introImagesJson = null;
        } else {
            try {
                this.introImagesJson = objectMapper.writeValueAsString(introImages);
            } catch (JsonProcessingException e) {
                this.introImagesJson = null;
            }
        }
    }

    // ===== suitableImages =====

    public List<Map<String, String>> getSuitableImages() {
        if (suitableImages != null) {
            return suitableImages;
        }
        if (suitableImagesJson == null || suitableImagesJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(suitableImagesJson, new TypeReference<List<Map<String, String>>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    public void setSuitableImages(List<Map<String, String>> suitableImages) {
        this.suitableImages = suitableImages;
        if (suitableImages == null || suitableImages.isEmpty()) {
            this.suitableImagesJson = null;
        } else {
            try {
                this.suitableImagesJson = objectMapper.writeValueAsString(suitableImages);
            } catch (JsonProcessingException e) {
                this.suitableImagesJson = null;
            }
        }
    }

    // ===== conditioningImages =====

    public List<String> getConditioningImages() {
        if (conditioningImages != null) {
            return conditioningImages;
        }
        if (conditioningImagesJson == null || conditioningImagesJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(conditioningImagesJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    public void setConditioningImages(List<String> conditioningImages) {
        this.conditioningImages = conditioningImages;
        if (conditioningImages == null || conditioningImages.isEmpty()) {
            this.conditioningImagesJson = null;
        } else {
            try {
                this.conditioningImagesJson = objectMapper.writeValueAsString(conditioningImages);
            } catch (JsonProcessingException e) {
                this.conditioningImagesJson = null;
            }
        }
    }

    // ===== processSteps =====

    public List<Map<String, String>> getProcessSteps() {
        if (processSteps != null) {
            return processSteps;
        }
        if (processStepsJson == null || processStepsJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(processStepsJson, new TypeReference<List<Map<String, String>>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    public void setProcessSteps(List<Map<String, String>> processSteps) {
        this.processSteps = processSteps;
        if (processSteps == null || processSteps.isEmpty()) {
            this.processStepsJson = null;
        } else {
            try {
                this.processStepsJson = objectMapper.writeValueAsString(processSteps);
            } catch (JsonProcessingException e) {
                this.processStepsJson = null;
            }
        }
    }

    // ===== contraindications =====

    public List<String> getContraindications() {
        if (contraindications != null) {
            return contraindications;
        }
        if (contraindicationsJson == null || contraindicationsJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(contraindicationsJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    public void setContraindications(List<String> contraindications) {
        this.contraindications = contraindications;
        if (contraindications == null || contraindications.isEmpty()) {
            this.contraindicationsJson = null;
        } else {
            try {
                this.contraindicationsJson = objectMapper.writeValueAsString(contraindications);
            } catch (JsonProcessingException e) {
                this.contraindicationsJson = null;
            }
        }
    }
}
