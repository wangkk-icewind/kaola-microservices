package com.kaola.product.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 价格计算VO
 */
@Data
public class PriceCalculationVO implements Serializable {
    private BigDecimal basePrice;
    private BigDecimal originalPrice;
    private BigDecimal finalPrice;
    private BigDecimal discount;
}
