package com.kaola.store.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 技师实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_masseur")
public class Masseur extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 所属门店ID
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 技师姓名
     */
    @TableField("name")
    private String name;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 性别 (1-男 2-女)
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 技师等级 (1-初级 2-中级 3-高级 4-专家)
     */
    @TableField("level")
    private Integer level;

    /**
     * 评分 (1.0-5.0)
     */
    @TableField("rating")
    private BigDecimal rating;

    /**
     * 资质证书 (JSON数组格式，存储证书图片URL)
     */
    @TableField("certifications")
    private String certifications;

    /**
     * 技能标签 (JSON数组格式，如["推拿","拔罐","刮痧"])
     */
    @TableField("skills")
    private String skills;

    /**
     * 状态 (0-禁用 1-正常 2-休息中)
     */
    @TableField("status")
    private Integer status;

    /**
     * 银行账户信息 (JSON格式，包含银行名称、卡号、户名)
     */
    @TableField("bank_account")
    private String bankAccount;
}
