package com.kaola.review.model.dto;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class ReviewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /** 技师ID，前端不传时由后端从订单获取 */
    private Long masseurId;

    /** 门店ID，前端不传时由后端从订单获取 */
    private Long storeId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1")
    @Max(value = 5, message = "评分最高为5")
    private Integer rating;

    private String content;

    private List<String> images;

    private Boolean anonymous;
}
