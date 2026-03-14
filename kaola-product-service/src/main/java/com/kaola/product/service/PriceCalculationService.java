package com.kaola.product.service;

import com.kaola.product.dto.PriceCalculationRequest;
import com.kaola.product.dto.PriceCalculationResult;

/**
 * 价格计算服务接口
 *
 * @author Kaola Team
 */
public interface PriceCalculationService {

    /**
     * 计算订单价格
     *
     * @param request 计算请求参数
     * @return 计算结果
     */
    PriceCalculationResult calculatePrice(PriceCalculationRequest request);

    /**
     * 预览价格（不保存，用于前端实时展示）
     *
     * @param request 计算请求参数
     * @return 计算结果
     */
    PriceCalculationResult previewPrice(PriceCalculationRequest request);
}
