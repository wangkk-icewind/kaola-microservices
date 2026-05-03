package com.kaola.product.service;

import com.kaola.product.model.entity.Product;

import java.util.List;

/**
 * 商品服务接口
 *
 * @author Kaola Team
 */
public interface ProductService {

    /**
     * 获取电子礼卡列表
     *
     * @return 电子礼卡列表
     */
    List<Product> getElectronicCards();

    /**
     * 获取项目礼卡列表
     *
     * @return 项目礼卡列表
     */
    List<Product> getProjectCards();

    /**
     * 获取实物商品列表
     *
     * @return 实物商品列表
     */
    List<Product> getPhysicalProducts();

    /**
     * 根据ID获取商品详情
     *
     * @param id 商品ID
     * @return 商品详情
     */
    Product getProductById(Long id);

    /**
     * 获取所有商品列表
     *
     * @return 所有商品列表
     */
    List<Product> getAllProducts();

    /**
     * 管理后台 - 获取商品列表（支持过滤）
     */
    List<Product> getAdminProductList(String type, Integer status, Integer isRecommended);

    /**
     * 创建商品
     */
    Product createProduct(Product product);

    /**
     * 更新商品
     */
    boolean updateProduct(Long id, Product product);

    /**
     * 删除商品（逻辑删除）
     */
    boolean deleteProduct(Long id);

    /**
     * 更新商品状态
     */
    boolean updateProductStatus(Long id, Integer status);
}
