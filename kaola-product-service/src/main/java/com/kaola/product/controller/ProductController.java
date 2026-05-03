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
import java.util.Map;

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

    @Operation(summary = "获取所有商品列表", description = "返回商品列表，支持过滤")
    @GetMapping("/list")
    public Result<List<Product>> getAllProducts(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer isRecommended) {
        log.info("API调用: 获取商品列表, type: {}, status: {}, isRecommended: {}", type, status, isRecommended);
        if (type == null && status == null && isRecommended == null) {
            List<Product> products = productService.getAllProducts();
            return Result.success(products);
        }
        List<Product> products = productService.getAdminProductList(type, status, isRecommended);
        return Result.success(products);
    }

    // ========== 管理后台接口 ==========

    @Operation(summary = "创建商品", description = "管理后台新建商品")
    @PostMapping
    public Result<Product> createProduct(@RequestBody Product product) {
        log.info("API调用: 创建商品, name: {}", product.getName());
        try {
            Product created = productService.createProduct(product);
            return Result.success(created);
        } catch (Exception e) {
            log.error("创建商品失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "更新商品", description = "管理后台修改商品")
    @PutMapping("/{id}")
    public Result<Boolean> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        log.info("API调用: 更新商品, id: {}", id);
        try {
            boolean success = productService.updateProduct(id, product);
            return success ? Result.success(true) : Result.error("更新失败");
        } catch (Exception e) {
            log.error("更新商品失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "删除商品", description = "管理后台删除商品")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteProduct(@PathVariable Long id) {
        log.info("API调用: 删除商品, id: {}", id);
        try {
            boolean success = productService.deleteProduct(id);
            return success ? Result.success(true) : Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除商品失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "更新商品状态", description = "管理后台上架/下架商品")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateProductStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        log.info("API调用: 更新商品状态, id: {}, status: {}", id, status);
        try {
            boolean success = productService.updateProductStatus(id, status);
            return success ? Result.success(true) : Result.error("更新失败");
        } catch (Exception e) {
            log.error("更新商品状态失败", e);
            return Result.error(e.getMessage());
        }
    }
}
