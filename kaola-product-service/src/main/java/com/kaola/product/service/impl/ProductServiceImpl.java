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
        wrapper.eq(Product::getStatus, 1)
                .orderByAsc(Product::getSortOrder)
                .orderByDesc(Product::getCreateTime);
        return productRepository.selectList(wrapper);
    }

    @Override
    public List<Product> getAdminProductList(String type, Integer status, Integer isRecommended) {
        log.info("管理后台获取商品列表, type: {}, status: {}, isRecommended: {}", type, status, isRecommended);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Product::getType, type);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        if (isRecommended != null) {
            wrapper.eq(Product::getIsRecommended, isRecommended);
        }
        wrapper.orderByAsc(Product::getSortOrder).orderByDesc(Product::getCreateTime);
        return productRepository.selectList(wrapper);
    }

    @Override
    public Product createProduct(Product product) {
        log.info("创建商品, name: {}", product.getName());
        if (product.getStatus() == null) product.setStatus(1);
        if (product.getSortOrder() == null) product.setSortOrder(100);
        productRepository.insert(product);
        return product;
    }

    @Override
    public boolean updateProduct(Long id, Product product) {
        log.info("更新商品, id: {}", id);
        product.setId(id);
        return productRepository.updateById(product) > 0;
    }

    @Override
    public boolean deleteProduct(Long id) {
        log.info("删除商品, id: {}", id);
        return productRepository.deleteById(id) > 0;
    }

    @Override
    public boolean updateProductStatus(Long id, Integer status) {
        log.info("更新商品状态, id: {}, status: {}", id, status);
        Product product = productRepository.selectById(id);
        if (product == null) throw new RuntimeException("商品不存在");
        product.setStatus(status);
        return productRepository.updateById(product) > 0;
    }

    @Override
    public boolean deductStock(Long productId, int quantity) {
        if (productId == null || quantity <= 0) return true;
        Product product = productRepository.selectById(productId);
        if (product == null) return false;
        // 仅实物商品扣库存；电子卡/项目卡视为无限，不扣
        if (!"PHYSICAL_PRODUCT".equals(product.getType())) return true;
        boolean ok = productRepository.deductStock(productId, quantity) > 0;
        log.info("扣减实物库存 productId={}, qty={}, 结果={}", productId, quantity, ok ? "成功" : "库存不足");
        return ok;
    }

    @Override
    public boolean restoreStock(Long productId, int quantity) {
        if (productId == null || quantity <= 0) return true;
        Product product = productRepository.selectById(productId);
        if (product == null || !"PHYSICAL_PRODUCT".equals(product.getType())) return true;
        boolean ok = productRepository.restoreStock(productId, quantity) > 0;
        log.info("回退实物库存 productId={}, qty={}, 结果={}", productId, quantity, ok);
        return ok;
    }
}
