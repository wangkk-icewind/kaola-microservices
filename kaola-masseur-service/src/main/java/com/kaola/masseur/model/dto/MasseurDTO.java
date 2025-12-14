package com.kaola.masseur.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

/**
 * 技师信息DTO
 *
 * @author Kaola Team
 */
@Data
public class MasseurDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 技能标签
     */
    private List<String> skills;

    /**
     * 资质证书
     */
    private List<String> certifications;

    /**
     * 银行账户信息
     */
    private String bankAccount;
}
