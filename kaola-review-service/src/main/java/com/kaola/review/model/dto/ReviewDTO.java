package com.kaola.review.model.dto;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 评价DTO
 *
 * @author Kaola Team
 */
@Data
public class ReviewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 技师ID
     */
    @NotNull(message = "技师ID不能为空")
    private Long masseurId;

    /**
     * 门店ID
     */
    @NotNull(message = "门店ID不能为空")
    private Long storeId;

    /**
     * 评分 (1-5)
     */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1")
    @Max(value = 5, message = "评分最高为5")
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片列表
     */
    private List<String> images;
}
