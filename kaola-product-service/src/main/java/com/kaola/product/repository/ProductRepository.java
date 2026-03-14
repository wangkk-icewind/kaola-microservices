package com.kaola.product.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.product.model.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface ProductRepository extends BaseMapper<Product> {
    // MyBatis-Plus provides basic CRUD operations automatically
    // Custom queries can be added here if needed
}
