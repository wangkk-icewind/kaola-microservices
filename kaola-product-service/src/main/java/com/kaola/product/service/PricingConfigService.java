package com.kaola.product.service;

import com.kaola.product.model.vo.PricingConfigVO;

/**
 * 定价配置服务接口
 *
 * @author Kaola Team
 */
public interface PricingConfigService {

    /**
     * 获取定价配置（用于小程序端）
     * 返回所有启用状态的定价配置
     *
     * @return 定价配置VO
     */
    PricingConfigVO getPricingConfig();
}
