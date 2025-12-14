package com.kaola.complaint.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 投诉VO
 *
 * @author Kaola Team
 */
@Data
public class ComplaintVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 投诉ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 投诉类型
     */
    private Integer type;

    /**
     * 类型描述
     */
    private String typeText;

    /**
     * 投诉内容
     */
    private String content;

    /**
     * 投诉图片
     */
    private List<String> images;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusText;

    /**
     * 处理回复
     */
    private String reply;

    /**
     * 回复时间
     */
    private LocalDateTime replyTime;

    /**
     * 提交时间
     */
    private LocalDateTime createTime;
}
