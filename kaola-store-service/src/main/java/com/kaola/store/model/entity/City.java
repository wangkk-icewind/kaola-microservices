package com.kaola.store.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_city")
public class City implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("city_code")
    private String cityCode;

    @TableField("city_name")
    private String cityName;

    @TableField("province")
    private String province;

    @TableField("city_level")
    private Integer cityLevel;

    @TableField("latitude")
    private BigDecimal latitude;

    @TableField("longitude")
    private BigDecimal longitude;

    @TableField("pinyin")
    private String pinyin;

    @TableField("pinyin_abbr")
    private String pinyinAbbr;

    /** 状态 0-下线 1-上线 */
    @TableField("status")
    private Integer status;

    /** 排序（数值越小越靠前） */
    @TableField("sort_order")
    private Integer sortOrder;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    private LocalDateTime updateTime;
}
