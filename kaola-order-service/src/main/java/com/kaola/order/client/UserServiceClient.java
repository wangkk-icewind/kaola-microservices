package com.kaola.order.client;

import com.kaola.common.core.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 用户服务 Feign 客户端
 *
 * @author Kaola Team
 */
@FeignClient(name = "kaola-user-service", url = "http://localhost:8082")
public interface UserServiceClient {

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/user/info")
    Result<UserVO> getUserInfo(@RequestHeader("X-User-Id") Long userId);

    /**
     * 用户信息 VO
     */
    class UserVO {
        private Long id;
        private String nickname;
        private String avatar;
        private String phone;
        private Integer gender;
        private java.time.LocalDateTime createTime;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
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

        public java.time.LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(java.time.LocalDateTime createTime) {
            this.createTime = createTime;
        }
    }
}
