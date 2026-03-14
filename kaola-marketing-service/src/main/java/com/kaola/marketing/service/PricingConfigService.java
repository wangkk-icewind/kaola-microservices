package com.kaola.marketing.service;

import com.kaola.marketing.model.dto.PriceCalculationRequest;
import com.kaola.marketing.model.vo.PriceCalculationVO;
import com.kaola.marketing.model.vo.PricingConfigVO;

/**
 * 定价配置服务接口
 *
 * @author Kaola Team
 */
public interface PricingConfigService {

    /**
     * 获取定价配置
     *
     * @return 定价配置
     */
    PricingConfigVO getPricingConfig();

    /**
     * 计算价格
     *
     * @param request 价格计算请求
     * @return 价格计算结果
     */
    PriceCalculationVO calculatePrice(PriceCalculationRequest request);
}
