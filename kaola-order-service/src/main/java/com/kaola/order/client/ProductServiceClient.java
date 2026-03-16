package com.kaola.order.client;

import com.kaola.common.core.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 项目服务 Feign 客户端
 *
 * @author Kaola Team
 */
@FeignClient(name = "kaola-product-service", url = "http://localhost:8085")
public interface ProductServiceClient {

    /**
     * 获取项目列表
     *
     * @param categoryId 分类ID
     * @param masseurId  技师ID
     * @return 项目列表
     */
    @GetMapping("/project/list")
    Result<List<ProjectVO>> getProjectList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long masseurId);

    /**
     * 获取项目详情
     *
     * @param id 项目ID
     * @return 项目详情
     */
    @GetMapping("/admin/project/detail/{id}")
    Result<ProjectVO> getProjectDetail(@PathVariable Long id);

    /**
     * 计算价格
     *
     * @param projectId 项目ID
     * @param masseurId 技师ID
     * @param storeId   门店ID
     * @param date      日期
     * @param time      时间
     * @return 价格计算结果
     */
    @GetMapping("/project/price")
    Result<PriceCalculationVO> calculatePrice(
            @RequestParam Long projectId,
            @RequestParam Long masseurId,
            @RequestParam Long storeId,
            @RequestParam LocalDate date,
            @RequestParam LocalTime time);

    /**
     * 项目信息 VO
     */
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    class ProjectVO {
        private Long id;
        private String name;
        private String description;
        private java.math.BigDecimal basePrice;
        private Integer duration;
        private Long categoryId;
        private String categoryName;
        private String imageUrl;
        private Integer status;
        private String createTime;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public java.math.BigDecimal getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(java.math.BigDecimal basePrice) {
            this.basePrice = basePrice;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }

    /**
     * 价格计算结果 VO
     */
    class PriceCalculationVO {
        private java.math.BigDecimal basePrice;
        private java.math.BigDecimal masseurFee;
        private java.math.BigDecimal peakHourFee;
        private java.math.BigDecimal discount;
        private java.math.BigDecimal finalPrice;

        // Getters and Setters
        public java.math.BigDecimal getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(java.math.BigDecimal basePrice) {
            this.basePrice = basePrice;
        }

        public java.math.BigDecimal getMasseurFee() {
            return masseurFee;
        }

        public void setMasseurFee(java.math.BigDecimal masseurFee) {
            this.masseurFee = masseurFee;
        }

        public java.math.BigDecimal getPeakHourFee() {
            return peakHourFee;
        }

        public void setPeakHourFee(java.math.BigDecimal peakHourFee) {
            this.peakHourFee = peakHourFee;
        }

        public java.math.BigDecimal getDiscount() {
            return discount;
        }

        public void setDiscount(java.math.BigDecimal discount) {
            this.discount = discount;
        }

        public java.math.BigDecimal getFinalPrice() {
            return finalPrice;
        }

        public void setFinalPrice(java.math.BigDecimal finalPrice) {
            this.finalPrice = finalPrice;
        }
    }
}
