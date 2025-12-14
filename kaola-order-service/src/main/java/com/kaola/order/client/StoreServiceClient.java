package com.kaola.order.client;

import com.kaola.common.core.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 门店服务 Feign 客户端
 *
 * @author Kaola Team
 */
@FeignClient(name = "kaola-store-service", url = "http://localhost:8083")
public interface StoreServiceClient {

    /**
     * 获取门店详情
     *
     * @param id 门店ID
     * @return 门店详情
     */
    @GetMapping("/store/{id}")
    Result<StoreVO> getStoreDetail(@PathVariable Long id);

    /**
     * 门店信息 VO
     */
    class StoreVO {
        private Long id;
        private String name;
        private String address;
        private String phone;
        private String city;
        private String district;
        private java.math.BigDecimal latitude;
        private java.math.BigDecimal longitude;
        private String businessHours;
        private String facilities;
        private Integer status;
        private java.time.LocalDateTime createTime;

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

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public java.math.BigDecimal getLatitude() {
            return latitude;
        }

        public void setLatitude(java.math.BigDecimal latitude) {
            this.latitude = latitude;
        }

        public java.math.BigDecimal getLongitude() {
            return longitude;
        }

        public void setLongitude(java.math.BigDecimal longitude) {
            this.longitude = longitude;
        }

        public String getBusinessHours() {
            return businessHours;
        }

        public void setBusinessHours(String businessHours) {
            this.businessHours = businessHours;
        }

        public String getFacilities() {
            return facilities;
        }

        public void setFacilities(String facilities) {
            this.facilities = facilities;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public java.time.LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(java.time.LocalDateTime createTime) {
            this.createTime = createTime;
        }
    }
}
