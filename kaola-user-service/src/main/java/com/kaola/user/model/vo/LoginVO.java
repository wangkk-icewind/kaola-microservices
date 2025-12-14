package com.kaola.user.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应VO
 *
 * @author Kaola Team
 */
@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private UserVO userInfo;

    /**
     * 是否新用户
     */
    private Boolean isNewUser;

    /**
     * 是否需要绑定手机
     */
    private Boolean needBindPhone;
}
