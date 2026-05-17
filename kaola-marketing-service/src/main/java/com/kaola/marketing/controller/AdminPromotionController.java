package com.kaola.marketing.controller;

import com.kaola.common.core.dto.PageVO;
import com.kaola.common.core.dto.Result;
import com.kaola.marketing.model.entity.Promotion;
import com.kaola.marketing.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台 - 促销活动管理接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "管理后台 - 促销活动管理", description = "促销活动的增删改查接口")
@RestController
@RequestMapping("/admin/promotion")
@RequiredArgsConstructor
@Validated
public class AdminPromotionController {

    private final PromotionService promotionService;

    /**
     * 分页查询促销活动列表
     */
    @Operation(summary = "分页查询促销活动列表", description = "支持名称搜索、类型筛选和状态筛选")
    @GetMapping("/list")
    public Result<PageVO<Promotion>> getPromotionList(
            @Parameter(description = "当前页", example = "1")
            @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "活动名称（模糊查询）")
            @RequestParam(required = false) String name,
            @Parameter(description = "活动类型 (1-满减 2-折扣 3-买赠 4-秒杀)")
            @RequestParam(required = false) Integer type,
            @Parameter(description = "状态 (0-禁用 1-启用)")
            @RequestParam(required = false) Integer status,
            @Parameter(description = "促销类别: banner=首页Banner促销, store_discount=门店时段折扣, 不传则返回全部")
            @RequestParam(required = false) String category) {

        log.info("分页查询促销活动列表, current: {}, pageSize: {}, name: {}, type: {}, status: {}, category: {}",
                current, pageSize, name, type, status, category);

        PageVO<Promotion> pageVO = promotionService.getPromotionList(current, pageSize, name, type, status, category);
        return Result.success(pageVO);
    }

    /**
     * 获取促销活动详情
     */
    @Operation(summary = "获取促销活动详情", description = "根据ID获取促销活动详细信息")
    @GetMapping("/detail/{id}")
    public Result<Promotion> getPromotionDetail(
            @Parameter(description = "促销活动ID", required = true)
            @PathVariable Long id) {

        log.info("获取促销活动详情, id: {}", id);

        Promotion promotion = promotionService.getPromotionDetail(id);
        return Result.success(promotion);
    }

    /**
     * 创建促销活动
     */
    @Operation(summary = "创建促销活动", description = "创建新的促销活动")
    @PostMapping("/create")
    public Result<Boolean> createPromotion(
            @Parameter(description = "促销活动信息", required = true)
            @RequestBody Promotion promotion) {

        log.info("创建促销活动, name: {}", promotion.getName());

        boolean success = promotionService.createPromotion(promotion);
        return Result.success(success);
    }

    /**
     * 更新促销活动
     */
    @Operation(summary = "更新促销活动", description = "更新促销活动信息")
    @PutMapping("/update")
    public Result<Boolean> updatePromotion(
            @Parameter(description = "促销活动信息", required = true)
            @RequestBody Promotion promotion) {

        log.info("更新促销活动, id: {}, name: {}", promotion.getId(), promotion.getName());

        boolean success = promotionService.updatePromotion(promotion);
        return Result.success(success);
    }

    /**
     * 删除促销活动
     */
    @Operation(summary = "删除促销活动", description = "软删除促销活动")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deletePromotion(
            @Parameter(description = "促销活动ID", required = true)
            @PathVariable Long id) {

        log.info("删除促销活动, id: {}", id);

        boolean success = promotionService.deletePromotion(id);
        return Result.success(success);
    }

    /**
     * 更新促销活动状态
     */
    @Operation(summary = "更新促销活动状态", description = "启用或禁用促销活动")
    @PutMapping("/updateStatus")
    public Result<Boolean> updatePromotionStatus(
            @RequestBody java.util.Map<String, Object> body) {

        Long id = body.get("id") instanceof Number ? ((Number) body.get("id")).longValue() : null;
        Integer status = body.get("status") instanceof Number ? ((Number) body.get("status")).intValue() : null;
        log.info("更新促销活动状态, id: {}, status: {}", id, status);

        boolean success = promotionService.updatePromotionStatus(id, status);
        return Result.success(success);
    }
}
