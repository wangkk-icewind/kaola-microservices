package com.kaola.user.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 登录请求DTO
 *
 * @author Kaola Team
 */
@Data
public class LoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 微信授权码
     */
    @NotBlank(message = "授权码不能为空")
    private String code;

    /** getPhoneNumber 按钮返回的 code，用于解析并绑定手机号（微信登录也拿手机号）；可选 */
    private String phoneCode;
}
