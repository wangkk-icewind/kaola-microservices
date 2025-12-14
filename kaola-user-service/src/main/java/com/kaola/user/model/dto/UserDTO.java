package com.kaola.user.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息DTO
 *
 * @author Kaola Team
 */
@Data
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别 (0-未知 1-男 2-女)
     */
    private Integer gender;

    /**
     * 城市
     */
    private String city;
}
