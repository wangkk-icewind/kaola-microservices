package com.kaola.product.service.impl;

import com.kaola.product.dto.PriceCalculationRequest;
import com.kaola.product.dto.PriceCalculationResult;
import com.kaola.product.model.entity.Masseur;
import com.kaola.product.model.entity.Project;
import com.kaola.product.repository.MasseurRepository;
import com.kaola.product.repository.ProjectRepository;
import com.kaola.product.service.MasseurLevelPricingService;
import com.kaola.product.service.PriceCalculationService;
import com.kaola.product.service.StorePricingService;
import com.kaola.product.service.TimeSlotPricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 价格计算服务实现类
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PriceCalculationServiceImpl implements PriceCalculationService {

    private final MasseurLevelPricingService masseurLevelPricingService;
    private final TimeSlotPricingService timeSlotPricingService;
    private final StorePricingService storePricingService;
    private final ProjectRepository projectRepository;
    private final MasseurRepository masseurRepository;

    @Override
    public PriceCalculationResult calculatePrice(PriceCalculationRequest request) {
        return doCalculate(request);
    }

    @Override
    public PriceCalculationResult previewPrice(PriceCalculationRequest request) {
        return doCalculate(request);
    }

    /**
     * 执行价格计算
     */
    private PriceCalculationResult doCalculate(PriceCalculationRequest request) {
        log.info("开始计算价格, request: {}", request);

        PriceCalculationResult result = new PriceCalculationResult();

        // 1. 获取项目基础价格
        Project project = projectRepository.selectById(request.getProjectId());
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        BigDecimal basePrice = project.getBasePrice();
        result.setBasePrice(basePrice);
        result.setOriginalPrice(basePrice); // 原价 = 项目基础价格，不叠加任何系数
        result.setDuration(request.getDuration() != null ? request.getDuration() : project.getDuration());

        // 2. 获取技师等级系数
        BigDecimal levelMultiplier = getLevelMultiplier(request);
        result.setLevelMultiplier(levelMultiplier);

        // 3. 获取时段系数
        BigDecimal timeSlotMultiplier = getTimeSlotMultiplier(request);
        result.setTimeSlotMultiplier(timeSlotMultiplier);

        // 4. 获取门店系数
        BigDecimal storeMultiplier = getStoreMultiplier(request);
        result.setStoreMultiplier(storeMultiplier);

        // 5. 计算服务价格 = 基础价格 × 技师等级系数 × 时段系数 × 门店系数
        BigDecimal servicePrice = basePrice
                .multiply(levelMultiplier)
                .multiply(timeSlotMultiplier)
                .multiply(storeMultiplier)
                .setScale(2, RoundingMode.HALF_UP);
        result.setServicePrice(servicePrice);

        // 6. 计算加钟费用
        BigDecimal extraCharge = calculateExtraCharge(project, request, levelMultiplier, timeSlotMultiplier, storeMultiplier);
        result.setExtraDuration(request.getExtraDuration() != null ? request.getExtraDuration() : 0);
        result.setExtraCharge(extraCharge);

        // 计算加钟单价（用于展示）
        if (project.getExtraPricePerMinute() != null) {
            result.setExtraPricePerMinute(project.getExtraPricePerMinute());
        } else {
            // 自动计算：基础价格 / 时长
            result.setExtraPricePerMinute(
                    basePrice.divide(BigDecimal.valueOf(project.getDuration()), 2, RoundingMode.HALF_UP)
            );
        }

        // 7. 计算小计
        BigDecimal subtotal = servicePrice.add(extraCharge);
        result.setSubtotal(subtotal);

        // 8. 计算优惠金额（TODO: 暂时设为0，后续实现优惠券和促销逻辑）
        BigDecimal discountAmount = BigDecimal.ZERO;
        result.setDiscountAmount(discountAmount);

        // 9. 计算最终价格
        BigDecimal finalPrice = subtotal.subtract(discountAmount);
        result.setFinalPrice(finalPrice);

        // 10. 生成价格说明
        String description = generatePriceDescription(result);
        result.setPriceDescription(description);

        log.info("价格计算完成, result: {}", result);
        return result;
    }

    /**
     * 获取技师等级系数
     */
    private BigDecimal getLevelMultiplier(PriceCalculationRequest request) {
        Integer level = request.getMasseurLevel();

        // 如果没有传等级，尝试从技师信息获取
        if (level == null && request.getMasseurId() != null) {
            Masseur masseur = masseurRepository.selectById(request.getMasseurId());
            if (masseur != null) {
                level = masseur.getLevel();
            }
        }

        // 如果还是没有等级，默认为中级（2级，系数1.0）
        if (level == null) {
            log.warn("未找到技师等级信息，使用默认等级2（中级）");
            level = 2;
        }

        return masseurLevelPricingService.getMultiplierByLevel(level);
    }

    /**
     * 获取时段系数
     */
    private BigDecimal getTimeSlotMultiplier(PriceCalculationRequest request) {
        LocalDateTime appointmentTime = request.getAppointmentTime();

        // 如果没有传预约时间，使用当前时间
        if (appointmentTime == null) {
            appointmentTime = LocalDateTime.now();
            log.warn("未传入预约时间，使用当前时间: {}", appointmentTime);
        }

        return timeSlotPricingService.getMultiplierByDateTime(appointmentTime);
    }

    /**
     * 获取门店系数
     */
    private BigDecimal getStoreMultiplier(PriceCalculationRequest request) {
        if (request.getStoreId() == null) {
            log.warn("未传入门店ID，使用默认系数1.0");
            return BigDecimal.ONE;
        }

        return storePricingService.getMultiplierByStoreId(request.getStoreId());
    }

    /**
     * 计算加钟费用
     */
    private BigDecimal calculateExtraCharge(Project project, PriceCalculationRequest request,
                                           BigDecimal levelMultiplier, BigDecimal timeSlotMultiplier,
                                           BigDecimal storeMultiplier) {
        Integer extraDuration = request.getExtraDuration();
        if (extraDuration == null || extraDuration <= 0) {
            return BigDecimal.ZERO;
        }

        // 获取加钟单价
        BigDecimal extraPricePerMinute;
        if (project.getExtraPricePerMinute() != null) {
            // 使用配置的加钟单价
            extraPricePerMinute = project.getExtraPricePerMinute();
        } else {
            // 自动计算：基础价格 / 时长
            extraPricePerMinute = project.getBasePrice()
                    .divide(BigDecimal.valueOf(project.getDuration()), 4, RoundingMode.HALF_UP);
        }

        // 加钟费用 = 加钟单价 × 加钟时长 × 技师等级系数 × 时段系数 × 门店系数
        BigDecimal extraCharge = extraPricePerMinute
                .multiply(BigDecimal.valueOf(extraDuration))
                .multiply(levelMultiplier)
                .multiply(timeSlotMultiplier)
                .multiply(storeMultiplier)
                .setScale(2, RoundingMode.HALF_UP);

        log.info("加钟费用计算: 单价={}, 时长={}分钟, 系数={}, 费用={}",
                extraPricePerMinute, extraDuration,
                levelMultiplier.multiply(timeSlotMultiplier).multiply(storeMultiplier),
                extraCharge);

        return extraCharge;
    }

    /**
     * 生成价格说明
     */
    private String generatePriceDescription(PriceCalculationResult result) {
        StringBuilder sb = new StringBuilder();

        sb.append("基础价格: ¥").append(result.getBasePrice());

        if (!result.getLevelMultiplier().equals(BigDecimal.ONE)) {
            sb.append(" × 技师等级系数").append(result.getLevelMultiplier());
        }

        if (!result.getTimeSlotMultiplier().equals(BigDecimal.ONE)) {
            sb.append(" × 时段系数").append(result.getTimeSlotMultiplier());
        }

        if (!result.getStoreMultiplier().equals(BigDecimal.ONE)) {
            sb.append(" × 门店系数").append(result.getStoreMultiplier());
        }

        sb.append(" = ¥").append(result.getServicePrice());

        if (result.getExtraDuration() > 0) {
            sb.append(" + 加钟费用¥").append(result.getExtraCharge())
              .append("(").append(result.getExtraDuration()).append("分钟)");
        }

        if (result.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            sb.append(" - 优惠¥").append(result.getDiscountAmount());
        }

        sb.append(" = 最终价格¥").append(result.getFinalPrice());

        return sb.toString();
    }
}
