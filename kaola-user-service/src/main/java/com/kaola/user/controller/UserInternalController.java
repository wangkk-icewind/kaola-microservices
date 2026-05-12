package com.kaola.user.controller;

import com.kaola.user.model.entity.User;
import com.kaola.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 供其他微服务内部调用（绕过网关鉴权），不对外暴露。
 */
@RestController
@RequestMapping("/user/internal")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserMapper userMapper;

    /**
     * 根据用户 ID 获取微信 openid（payment-service 发起 JSAPI 支付时调用）
     */
    @GetMapping("/openid/{userId}")
    public String getOpenId(@PathVariable Long userId) {
        User user = userMapper.selectById(userId);
        return user != null ? user.getOpenId() : null;
    }
}
