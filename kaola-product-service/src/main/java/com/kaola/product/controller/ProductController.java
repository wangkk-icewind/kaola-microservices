package com.kaola.product.controller;

import com.kaola.product.dto.Result;
import com.kaola.product.model.entity.Product;
import com.kaola.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器 - 用户端API
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "商品管理", description = "商品相关接口（电子礼卡、项目礼卡、实物商品）")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "获取电子礼卡列表", description = "返回所有上架的电子礼卡")
    @GetMapping("/electronic-cards")
    public Result<List<Product>> getElectronicCards() {
        log.info("API调用: 获取电子礼卡列表");
        List<Product> products = productService.getElectronicCards();
        return Result.success(products);
    }

    @Operation(summary = "获取项目礼卡列表", description = "返回所有上架的项目礼卡")
    @GetMapping("/project-cards")
    public Result<List<Product>> getProjectCards() {
        log.info("API调用: 获取项目礼卡列表");
        List<Product> products = productService.getProjectCards();
        return Result.success(products);
    }

    @Operation(summary = "获取实物商品列表", description = "返回所有上架的实物商品")
    @GetMapping("/physical-products")
    public Result<List<Product>> getPhysicalProducts() {
        log.info("API调用: 获取实物商品列表");
        List<Product> products = productService.getPhysicalProducts();
        return Result.success(products);
    }

    @Operation(summary = "获取商品详情", description = "根据商品ID获取详细信息")
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        log.info("API调用: 获取商品详情, id: {}", id);
        Product product = productService.getProductById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    @Operation(summary = "获取所有商品列表", description = "返回所有上架的商品（不区分类型）")
    @GetMapping("/list")
    public Result<List<Product>> getAllProducts() {
        log.info("API调用: 获取所有商品列表");
        List<Product> products = productService.getAllProducts();
        return Result.success(products);
    }
}
