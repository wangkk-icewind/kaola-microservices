package com.kaola.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.product.model.entity.Product;
import com.kaola.product.repository.ProductRepository;
import com.kaola.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品服务实现类
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private static final String TYPE_ELECTRONIC_CARD = "ELECTRONIC_CARD";
    private static final String TYPE_PROJECT_CARD = "PROJECT_CARD";
    private static final String TYPE_PHYSICAL_PRODUCT = "PHYSICAL_PRODUCT";

    @Override
    public List<Product> getElectronicCards() {
        log.info("获取电子礼卡列表");
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getType, TYPE_ELECTRONIC_CARD)
                .eq(Product::getStatus, 1) // 只返回上架的商品
                .orderByAsc(Product::getSortOrder)
                .orderByDesc(Product::getCreateTime);
        return productRepository.selectList(wrapper);
    }

    @Override
    public List<Product> getProjectCards() {
        log.info("获取项目礼卡列表");
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getType, TYPE_PROJECT_CARD)
                .eq(Product::getStatus, 1) // 只返回上架的商品
                .orderByAsc(Product::getSortOrder)
                .orderByDesc(Product::getCreateTime);
        return productRepository.selectList(wrapper);
    }

    @Override
    public List<Product> getPhysicalProducts() {
        log.info("获取实物商品列表");
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getType, TYPE_PHYSICAL_PRODUCT)
                .eq(Product::getStatus, 1) // 只返回上架的商品
                .orderByAsc(Product::getSortOrder)
                .orderByDesc(Product::getCreateTime);
        return productRepository.selectList(wrapper);
    }

    @Override
    public Product getProductById(Long id) {
        log.info("获取商品详情, id: {}", id);
        return productRepository.selectById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        log.info("获取所有商品列表");
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1) // 只返回上架的商品
                .orderByAsc(Product::getSortOrder)
                .orderByDesc(Product::getCreateTime);
        return productRepository.selectList(wrapper);
    }
}
