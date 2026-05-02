package com.kaola.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.product.model.entity.MasseurLevelPricing;
import com.kaola.product.repository.MasseurLevelPricingRepository;
import com.kaola.product.service.MasseurLevelPricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 技师等级价格系数配置服务实现类
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MasseurLevelPricingServiceImpl implements MasseurLevelPricingService {

    private final MasseurLevelPricingRepository masseurLevelPricingRepository;

    @Override
    public List<MasseurLevelPricing> getList() {
        log.info("获取所有技师等级价格系数配置");
        return masseurLevelPricingRepository.findAll();
    }

    @Override
    public List<MasseurLevelPricing> getEnabledList() {
        log.info("获取所有启用的技师等级价格系数配置");
        return masseurLevelPricingRepository.findAllEnabled();
    }

    @Override
    public MasseurLevelPricing getById(Long id) {
        log.info("根据ID获取技师等级价格系数配置, id: {}", id);
        return masseurLevelPricingRepository.selectById(id);
    }

    @Override
    public BigDecimal getMultiplierByLevel(Integer level) {
        log.info("根据等级获取价格系数, level: {}", level);
        BigDecimal multiplier = masseurLevelPricingRepository.getMultiplierByLevel(level);
        return multiplier != null ? multiplier : BigDecimal.ONE;
    }

    @Override
    @Transactional
    public boolean create(MasseurLevelPricing pricing) {
        log.info("创建技师等级价格系数配置, pricing: {}", pricing);

        // 检查等级是否已存在
        MasseurLevelPricing existing = masseurLevelPricingRepository.findByLevel(pricing.getLevel());
        if (existing != null) {
            throw new RuntimeException("该等级的价格系数配置已存在");
        }

        // 设置默认值
        if (pricing.getStatus() == null) {
            pricing.setStatus(1);
        }

        return masseurLevelPricingRepository.insert(pricing) > 0;
    }

    @Override
    @Transactional
    public boolean update(Long id, MasseurLevelPricing pricing) {
        log.info("更新技师等级价格系数配置, id: {}, pricing: {}", id, pricing);

        MasseurLevelPricing existing = masseurLevelPricingRepository.selectById(id);
        if (existing == null) {
            throw new RuntimeException("价格系数配置不存在");
        }

        // 如果修改了等级，检查新等级是否已存在
        if (pricing.getLevel() != null && !pricing.getLevel().equals(existing.getLevel())) {
            MasseurLevelPricing duplicate = masseurLevelPricingRepository.findByLevel(pricing.getLevel());
            if (duplicate != null) {
                throw new RuntimeException("该等级的价格系数配置已存在");
            }
        }

        pricing.setId(id);
        // 保留原有 level，不被前端传入的 null 覆盖
        if (pricing.getLevel() == null) {
            pricing.setLevel(existing.getLevel());
        }
        return masseurLevelPricingRepository.updateById(pricing) > 0;
    }

    @Override
    @Transactional
    public boolean batchUpdate(List<MasseurLevelPricing> pricingList) {
        log.info("批量更新技师等级价格系数配置, count: {}", pricingList.size());

        for (MasseurLevelPricing pricing : pricingList) {
            if (pricing.getId() != null) {
                masseurLevelPricingRepository.updateById(pricing);
            }
        }

        return true;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        log.info("删除技师等级价格系数配置, id: {}", id);
        return masseurLevelPricingRepository.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        log.info("更新技师等级价格系数配置状态, id: {}, status: {}", id, status);

        MasseurLevelPricing pricing = masseurLevelPricingRepository.selectById(id);
        if (pricing == null) {
            throw new RuntimeException("价格系数配置不存在");
        }

        pricing.setStatus(status);
        return masseurLevelPricingRepository.updateById(pricing) > 0;
    }
}
