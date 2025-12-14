package com.kaola.store.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 门店列表VO
 *
 * @author Kaola Team
 */
@Data
public class StoreVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 门店ID
     */
    private Long id;

    /**
     * 门店名称
     */
    private String name;

    /**
     * 门店图片
     */
    private String image;

    /**
     * 地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 距离（米）
     */
    private Double distance;

    /**
     * 距离描述
     */
    private String distanceText;

    /**
     * 营业时间
     */
    private String businessHours;

    /**
     * 是否营业中
     */
    private Boolean isOpen;

    /**
     * 技师数量
     */
    private Integer masseurCount;
}
