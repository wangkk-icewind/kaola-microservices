package com.kaola.user.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息VO
 *
 * @author Kaola Team
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 手机号（脱敏）
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 城市
     */
    private String city;

    /**
     * 积分
     */
    private Integer point;

    /**
     * 会员等级
     */
    private Integer level;

    /**
     * 会员等级名称
     */
    private String levelName;

    /**
     * 优惠券数量
     */
    private Integer couponCount;

    /**
     * 订单数量
     */
    private Integer orderCount;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;
}
