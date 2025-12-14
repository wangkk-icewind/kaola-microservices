package com.kaola.review.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价VO
 *
 * @author Kaola Team
 */
@Data
public class ReviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评价ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 技师ID
     */
    private Long masseurId;

    /**
     * 技师名称
     */
    private String masseurName;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片
     */
    private List<String> images;

    /**
     * 商家回复
     */
    private String reply;

    /**
     * 回复时间
     */
    private LocalDateTime replyTime;

    /**
     * 是否匿名
     */
    private Boolean anonymous;

    /**
     * 评价时间
     */
    private LocalDateTime createTime;
}
