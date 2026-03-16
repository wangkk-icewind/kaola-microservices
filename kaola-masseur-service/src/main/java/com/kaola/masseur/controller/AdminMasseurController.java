package com.kaola.masseur.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.Result;
import com.kaola.common.model.vo.PageVO;
import com.kaola.masseur.model.entity.Masseur;
import com.kaola.masseur.model.entity.MasseurEarning;
import com.kaola.masseur.mapper.MasseurMapper;
import com.kaola.masseur.service.MasseurEarningService;
import com.kaola.masseur.service.MasseurProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 技师管理接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "管理后台 - 技师管理", description = "技师的增删改查接口")
@RestController
@RequestMapping("/admin/masseur")
@RequiredArgsConstructor
public class AdminMasseurController {

    private final MasseurMapper masseurMapper;
    private final MasseurEarningService masseurEarningService;
    private final MasseurProjectService masseurProjectService;

    /**
     * 分页查询技师列表
     */
    @Operation(summary = "分页查询技师列表", description = "支持名称搜索、门店筛选和状态筛选")
    @GetMapping("/list")
    public Result<PageVO<Masseur>> getMasseurList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Integer status) {

        log.info("分页查询技师列表, current: {}, pageSize: {}, name: {}, storeId: {}, status: {}",
                 current, pageSize, name, storeId, status);

        Page<Masseur> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Masseur> wrapper = new LambdaQueryWrapper<>();

        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(Masseur::getName, name.trim());
        }

        if (storeId != null) {
            // Note: Masseur entity doesn't have storeId in current model
            // This would need to be added if store filtering is required
        }

        if (status != null) {
            wrapper.eq(Masseur::getStatus, status);
        }

        wrapper.orderByDesc(Masseur::getCreateTime);

        IPage<Masseur> pageResult = masseurMapper.selectPage(page, wrapper);
        PageVO<Masseur> pageVO = PageVO.of(pageResult);

        return Result.success(pageVO);
    }

    /**
     * 根据门店获取技师列表
     */
    @Operation(summary = "根据门店获取技师列表", description = "获取指定门店的所有技师")
    @GetMapping("/byStore/{storeId}")
    public Result<List<Masseur>> getMasseursByStore(@PathVariable Long storeId) {
        log.info("根据门店获取技师列表, storeId: {}", storeId);

        // Note: Current Masseur entity doesn't have storeId field
        // Return all active masseurs for now
        LambdaQueryWrapper<Masseur> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Masseur::getStatus, 1)
               .orderByDesc(Masseur::getRating);

        List<Masseur> masseurs = masseurMapper.selectList(wrapper);
        return Result.success(masseurs);
    }

    /**
     * 获取技师详情
     */
    @Operation(summary = "获取技师详情", description = "根据ID获取技师详细信息")
    @GetMapping("/detail/{id}")
    public Result<Masseur> getMasseurDetail(@PathVariable Long id) {
        log.info("获取技师详情, id: {}", id);

        Masseur masseur = masseurMapper.selectById(id);
        if (masseur == null || masseur.getDeleted() == 1) {
            return Result.error("技师不存在");
        }

        return Result.success(masseur);
    }

    /**
     * 创建技师
     */
    @Operation(summary = "创建技师", description = "新建技师信息")
    @PostMapping("/create")
    public Result<Boolean> createMasseur(@RequestBody Masseur masseur) {
        log.info("创建技师, name: {}", masseur.getName());

        if (masseur.getStatus() == null) {
            masseur.setStatus(1);
        }

        int rows = masseurMapper.insert(masseur);
        return rows > 0 ? Result.success(true) : Result.error("创建失败");
    }

    /**
     * 更新技师
     */
    @Operation(summary = "更新技师", description = "修改技师信息")
    @PutMapping("/update")
    public Result<Boolean> updateMasseur(@RequestBody Masseur masseur) {
        log.info("更新技师, id: {}, name: {}", masseur.getId(), masseur.getName());

        if (masseur.getId() == null) {
            return Result.error("技师ID不能为空");
        }

        Masseur existing = masseurMapper.selectById(masseur.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("技师不存在");
        }

        int rows = masseurMapper.updateById(masseur);
        return rows > 0 ? Result.success(true) : Result.error("更新失败");
    }

    /**
     * 删除技师（逻辑删除）
     */
    @Operation(summary = "删除技师", description = "逻辑删除技师")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteMasseur(@PathVariable Long id) {
        log.info("删除技师, id: {}", id);

        Masseur masseur = masseurMapper.selectById(id);
        if (masseur == null || masseur.getDeleted() == 1) {
            return Result.error("技师不存在");
        }

        masseur.setDeleted(1);
        int rows = masseurMapper.updateById(masseur);
        return rows > 0 ? Result.success(true) : Result.error("删除失败");
    }

    /**
     * 更新技师状态
     */
    @Operation(summary = "更新技师状态", description = "更改技师在职状态")
    @PutMapping("/updateStatus")
    public Result<Boolean> updateMasseurStatus(@RequestBody Masseur request) {
        log.info("更新技师状态, id: {}, status: {}", request.getId(), request.getStatus());

        if (request.getId() == null || request.getStatus() == null) {
            return Result.error("参数不完整");
        }

        Masseur masseur = masseurMapper.selectById(request.getId());
        if (masseur == null || masseur.getDeleted() == 1) {
            return Result.error("技师不存在");
        }

        masseur.setStatus(request.getStatus());
        int rows = masseurMapper.updateById(masseur);
        return rows > 0 ? Result.success(true) : Result.error("更新失败");
    }

    /**
     * 获取技师的项目列表
     */
    @Operation(summary = "获取技师项目列表", description = "获取指定技师可服务的项目ID列表")
    @GetMapping("/{masseurId}/projects")
    public Result<List<Long>> getMasseurProjects(@PathVariable Long masseurId) {
        log.info("获取技师项目列表, masseurId: {}", masseurId);
        List<Long> projectIds = masseurProjectService.getMasseurProjectIds(masseurId);
        return Result.success(projectIds);
    }

    /**
     * 更新技师的项目列表
     */
    @Operation(summary = "更新技师项目列表", description = "批量设置技师可服务的项目")
    @PostMapping("/{masseurId}/projects")
    public Result<Boolean> updateMasseurProjects(
            @PathVariable Long masseurId,
            @RequestBody Map<String, List<Long>> body) {
        log.info("更新技师项目列表, masseurId: {}", masseurId);
        List<Long> projectIds = body.getOrDefault("projectIds", Collections.emptyList());
        masseurProjectService.setMasseurProjects(masseurId, projectIds);
        return Result.success(true);
    }

    /**
     * 获取技师收益记录
     */
    @Operation(summary = "获取技师收益记录", description = "分页查询指定技师的收益记录")
    @GetMapping("/earnings/{masseurId}")
    public Result<PageVO<MasseurEarning>> getMasseurEarnings(
            @PathVariable Long masseurId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize) {

        log.info("获取技师收益记录, masseurId: {}, current: {}, pageSize: {}", masseurId, current, pageSize);

        // 验证技师是否存在
        Masseur masseur = masseurMapper.selectById(masseurId);
        if (masseur == null || masseur.getDeleted() == 1) {
            return Result.error("技师不存在");
        }

        PageVO<MasseurEarning> pageVO = masseurEarningService.getEarningsByMasseur(masseurId, current, pageSize);
        return Result.success(pageVO);
    }
}
