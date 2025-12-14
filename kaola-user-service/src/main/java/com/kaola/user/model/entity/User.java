package com.kaola.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 微信openId
     */
    @TableField("open_id")
    private String openId;

    /**
     * 微信unionId
     */
    @TableField("union_id")
    private String unionId;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 性别 (0-未知 1-男 2-女)
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 积分
     */
    @TableField("point")
    private Integer point;

    /**
     * 会员等级 (1-普通会员 2-银卡会员 3-金卡会员 4-钻石会员)
     */
    @TableField("level")
    private Integer level;

    /**
     * 状态 (0-禁用 1-正常)
     */
    @TableField("status")
    private Integer status;
}
