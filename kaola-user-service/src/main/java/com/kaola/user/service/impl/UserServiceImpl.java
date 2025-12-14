package com.kaola.user.service.impl;

import com.kaola.user.model.dto.UserDTO;
import com.kaola.user.model.vo.LoginVO;
import com.kaola.user.model.vo.UserVO;
import com.kaola.user.service.UserService;
import com.kaola.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类 - Mock实现
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtUtil jwtUtil;

    @Override
    public LoginVO login(String code) {
        log.info("用户微信登录, code: {}", code);

        // Mock实现 - 返回模拟数据
        // 使用固定的用户ID=1
        Long userId = 1L;

        LoginVO loginVO = new LoginVO();
        // 使用真正的JWT token
        loginVO.setToken(jwtUtil.generateToken(userId));
        loginVO.setTokenType("Bearer");
        loginVO.setExpiresIn(86400L);
        loginVO.setIsNewUser(false);
        loginVO.setNeedBindPhone(false);

        // Mock用户信息
        UserVO userVO = new UserVO();
        userVO.setId(userId);
        userVO.setNickname("MockUser");
        userVO.setAvatar("https://example.com/avatar.png");
        loginVO.setUserInfo(userVO);

        return loginVO;
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        log.info("获取用户信息, userId: {}", userId);

        // Mock实现 - 返回模拟数据
        UserVO userVO = new UserVO();
        userVO.setId(userId);
        userVO.setNickname("MockUser");
        userVO.setAvatar("https://example.com/avatar.png");
        userVO.setPhone("13800138000");
        userVO.setGender(1);

        return userVO;
    }

    @Override
    public boolean updateUserInfo(Long userId, UserDTO dto) {
        log.info("更新用户信息, userId: {}, dto: {}", userId, dto);

        // Mock实现 - 直接返回成功
        return true;
    }

    @Override
    public boolean bindPhone(Long userId, String phone, String code) {
        log.info("绑定手机号, userId: {}, phone: {}", userId, phone);

        // Mock实现 - 直接返回成功
        return true;
    }
}
