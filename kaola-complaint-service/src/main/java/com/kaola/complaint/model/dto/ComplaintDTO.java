package com.kaola.complaint.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 投诉DTO
 *
 * @author Kaola Team
 */
@Data
public class ComplaintDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 投诉类型
     */
    @NotNull(message = "投诉类型不能为空")
    private Integer type;

    /**
     * 投诉内容
     */
    @NotBlank(message = "投诉内容不能为空")
    private String content;

    /**
     * 图片列表
     */
    private List<String> images;
}
