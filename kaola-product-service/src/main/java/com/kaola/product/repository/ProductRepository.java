package com.kaola.product.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.product.model.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 商品数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface ProductRepository extends BaseMapper<Product> {

    /**
     * 原子扣减实物商品库存（仅 PHYSICAL_PRODUCT，且库存充足时才扣，防超卖）。
     * @return 影响行数：1=扣减成功，0=库存不足或非实物商品
     */
    @Update("UPDATE t_product SET stock = stock - #{qty}, sales_count = sales_count + #{qty} " +
            "WHERE id = #{id} AND type = 'PHYSICAL_PRODUCT' AND stock >= #{qty} AND deleted = 0")
    int deductStock(@Param("id") Long id, @Param("qty") int qty);

    /**
     * 回退实物商品库存（取消订单时）。
     */
    @Update("UPDATE t_product SET stock = stock + #{qty}, " +
            "sales_count = CASE WHEN sales_count >= #{qty} THEN sales_count - #{qty} ELSE 0 END " +
            "WHERE id = #{id} AND type = 'PHYSICAL_PRODUCT' AND deleted = 0")
    int restoreStock(@Param("id") Long id, @Param("qty") int qty);
}
