package com.kaola.user.service;

import com.kaola.user.model.dto.UserDTO;
import com.kaola.user.model.entity.User;
import com.kaola.user.model.vo.LoginVO;
import com.kaola.user.model.vo.UserVO;

/**
 * 用户服务接口
 *
 * @author Kaola Team
 */
public interface UserService {

    /**
     * 微信登录
     *
     * @param code 微信授权码
     * @return 登录结果
     */
    LoginVO login(String code);

    /**
     * 发送短信验证码（存入Redis，TTL=5分钟）
     *
     * @param phone 手机号
     * @return 是否发送成功
     */
    boolean sendVerifyCode(String phone);

    /**
     * 手机号登录
     *
     * @param phone      手机号
     * @param verifyCode 验证码
     * @return 登录结果
     */
    LoginVO phoneLogin(String phone, String verifyCode);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserInfo(Long userId);

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param dto    更新数据
     * @return 是否成功
     */
    boolean updateUserInfo(Long userId, UserDTO dto);

    /**
     * 绑定手机号
     *
     * @param userId 用户ID
     * @param phone  手机号
     * @param code   验证码
     * @return 是否成功
     */
    boolean bindPhone(Long userId, String phone, String code);
}
