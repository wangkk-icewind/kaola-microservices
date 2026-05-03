package com.kaola.product.service.impl;

import com.kaola.product.model.entity.Store;
import com.kaola.product.model.entity.StorePricing;
import com.kaola.product.repository.StoreRepository;
import com.kaola.product.repository.StorePricingRepository;
import com.kaola.product.service.StorePricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 门店价格系数配置服务实现类
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorePricingServiceImpl implements StorePricingService {

    private final StorePricingRepository storePricingRepository;
    private final StoreRepository storeRepository;

    @Override
    public List<StorePricing> getList() {
        log.info("获取所有门店价格系数配置");
        return storePricingRepository.findAll();
    }

    @Override
    public List<StorePricing> getEnabledList() {
        log.info("获取所有启用的门店价格系数配置");
        return storePricingRepository.findAllEnabled();
    }

    @Override
    public StorePricing getById(Long id) {
        log.info("根据ID获取门店价格系数配置, id: {}", id);
        return storePricingRepository.selectById(id);
    }

    @Override
    public BigDecimal getMultiplierByStoreId(Long storeId) {
        log.info("根据门店ID获取价格系数, storeId: {}", storeId);
        BigDecimal multiplier = storePricingRepository.getMultiplierByStoreId(storeId);
        return multiplier != null ? multiplier : BigDecimal.ONE;
    }

    @Override
    public StorePricing getByStoreId(Long storeId) {
        log.info("根据门店ID获取门店价格系数配置, storeId: {}", storeId);
        return storePricingRepository.findByStoreId(storeId);
    }

    @Override
    @Transactional
    public boolean create(StorePricing pricing) {
        log.info("创建门店价格系数配置, pricing: {}", pricing);

        // 检查门店是否存在
        Store store = storeRepository.selectById(pricing.getStoreId());
        if (store == null) {
            throw new RuntimeException("门店不存在");
        }

        // 检查门店是否已配置
        StorePricing existing = storePricingRepository.findByStoreId(pricing.getStoreId());
        if (existing != null) {
            throw new RuntimeException("该门店的价格系数配置已存在");
        }

        // 设置门店名称（冗余字段）
        pricing.setStoreName(store.getName());

        // 设置默认值
        if (pricing.getStatus() == null) {
            pricing.setStatus(1);
        }

        return storePricingRepository.insert(pricing) > 0;
    }

    @Override
    @Transactional
    public boolean update(Long id, StorePricing pricing) {
        log.info("更新门店价格系数配置, id: {}, pricing: {}", id, pricing);

        StorePricing existing = storePricingRepository.selectById(id);
        if (existing == null) {
            throw new RuntimeException("价格系数配置不存在");
        }

        // 如果修改了门店，检查新门店是否已存在配置
        if (pricing.getStoreId() != null && !pricing.getStoreId().equals(existing.getStoreId())) {
            Store store = storeRepository.selectById(pricing.getStoreId());
            if (store == null) {
                throw new RuntimeException("门店不存在");
            }

            StorePricing duplicate = storePricingRepository.findByStoreId(pricing.getStoreId());
            if (duplicate != null) {
                throw new RuntimeException("该门店的价格系数配置已存在");
            }

            // 更新门店名称
            pricing.setStoreName(store.getName());
        }

        pricing.setId(id);
        return storePricingRepository.updateById(pricing) > 0;
    }

    @Override
    @Transactional
    public boolean batchUpdate(List<StorePricing> pricingList) {
        log.info("批量更新门店价格系数配置, count: {}", pricingList.size());

        for (StorePricing pricing : pricingList) {
            if (pricing.getId() != null) {
                // 如果有门店ID，更新门店名称
                if (pricing.getStoreId() != null) {
                    Store store = storeRepository.selectById(pricing.getStoreId());
                    if (store != null) {
                        pricing.setStoreName(store.getName());
                    }
                }
                storePricingRepository.updateById(pricing);
            }
        }

        return true;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        log.info("删除门店价格系数配置, id: {}", id);
        return storePricingRepository.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        log.info("更新门店价格系数配置状态, id: {}, status: {}", id, status);

        StorePricing pricing = storePricingRepository.selectById(id);
        if (pricing == null) {
            throw new RuntimeException("价格系数配置不存在");
        }

        pricing.setStatus(status);
        return storePricingRepository.updateById(pricing) > 0;
    }
}
