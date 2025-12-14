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
}
