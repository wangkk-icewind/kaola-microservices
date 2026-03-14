package com.kaola.store.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 城市实体
 *
 * @author Kaola Team
 */
@Data
@TableName("t_city")
public class City implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 城市代码
     */
    @TableField("city_code")
    private String cityCode;

    /**
     * 城市名称
     */
    @TableField("city_name")
    private String cityName;

    /**
     * 省份
     */
    @TableField("province")
    private String province;

    /**
     * 城市级别 (1-直辖市, 2-省会, 3-地级市)
     */
    @TableField("city_level")
    private Integer cityLevel;

    /**
     * 纬度
     */
    @TableField("latitude")
    private BigDecimal latitude;

    /**
     * 经度
     */
    @TableField("longitude")
    private BigDecimal longitude;

    /**
     * 拼音全拼
     */
    @TableField("pinyin")
    private String pinyin;

    /**
     * 拼音首字母
     */
    @TableField("pinyin_abbr")
    private String pinyinAbbr;
}
