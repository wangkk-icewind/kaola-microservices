package com.kaola.marketing.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 技师等级定价实体
 *
 * @author Kaola Team
 */
@Data
@TableName("t_masseur_level_pricing")
public class MasseurLevelPricing implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 技师等级 (1-初级 2-中级 3-高级 4-特级)
     */
    private Integer level;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 价格倍率
     */
    private BigDecimal multiplier;

    /**
     * 状态 (1-启用 0-禁用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
