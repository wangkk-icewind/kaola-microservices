package com.kaola.order.client;

import com.kaola.common.core.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 技师服务 Feign 客户端
 *
 * @author Kaola Team
 */
@FeignClient(name = "kaola-masseur-service", url = "http://localhost:8084")
public interface MasseurServiceClient {

    /**
     * 获取技师详情
     *
     * @param id 技师ID
     * @return 技师详情
     */
    @GetMapping("/masseur/detail/{id}")
    Result<MasseurVO> getMasseurDetail(@PathVariable Long id);

    /**
     * 技师信息 VO
     */
    class MasseurVO {
        private Long id;
        private String name;
        private String avatar;
        private String phone;
        private Integer gender;
        private Integer age;
        private String introduction;
        private String specialty;
        private Integer level;
        private java.math.BigDecimal rating;
        private Integer serviceCount;
        private Long storeId;
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

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Integer getGender() {
            return gender;
        }

        public void setGender(Integer gender) {
            this.gender = gender;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public String getSpecialty() {
            return specialty;
        }

        public void setSpecialty(String specialty) {
            this.specialty = specialty;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public java.math.BigDecimal getRating() {
            return rating;
        }

        public void setRating(java.math.BigDecimal rating) {
            this.rating = rating;
        }

        public Integer getServiceCount() {
            return serviceCount;
        }

        public void setServiceCount(Integer serviceCount) {
            this.serviceCount = serviceCount;
        }

        public Long getStoreId() {
            return storeId;
        }

        public void setStoreId(Long storeId) {
            this.storeId = storeId;
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
