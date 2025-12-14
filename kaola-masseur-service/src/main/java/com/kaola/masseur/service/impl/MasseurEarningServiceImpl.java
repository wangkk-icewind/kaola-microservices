package com.kaola.masseur.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.model.vo.PageVO;
import com.kaola.masseur.mapper.MasseurEarningMapper;
import com.kaola.masseur.model.entity.MasseurEarning;
import com.kaola.masseur.service.MasseurEarningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * 技师收益服务实现类
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MasseurEarningServiceImpl implements MasseurEarningService {

    private final MasseurEarningMapper masseurEarningMapper;

    @Override
    public PageVO<MasseurEarning> getEarningsByMasseur(Long masseurId, Long current, Long pageSize) {
        log.info("分页查询技师收益记录, masseurId: {}, current: {}, pageSize: {}", masseurId, current, pageSize);

        Page<MasseurEarning> page = new Page<>(current, pageSize);
        IPage<MasseurEarning> pageResult = masseurEarningMapper.findByMasseurId(page, masseurId);

        // 使用静态方法转换为 PageVO
        return PageVO.of(pageResult);
    }

    @Override
    public PageVO<MasseurEarning> getEarningsList(Long masseurId, String startDate, String endDate, Long current, Long pageSize) {
        log.info("分页查询收益列表, masseurId: {}, startDate: {}, endDate: {}, current: {}, pageSize: {}",
                masseurId, startDate, endDate, current, pageSize);

        Page<MasseurEarning> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<MasseurEarning> wrapper = new LambdaQueryWrapper<>();

        // 可选筛选条件
        if (masseurId != null) {
            wrapper.eq(MasseurEarning::getMasseurId, masseurId);
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                wrapper.ge(MasseurEarning::getCreateTime, LocalDate.parse(startDate).atStartOfDay());
            } catch (DateTimeParseException e) {
                log.warn("Invalid start date format: {}", startDate);
            }
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                wrapper.le(MasseurEarning::getCreateTime, LocalDate.parse(endDate).atTime(23, 59, 59));
            } catch (DateTimeParseException e) {
                log.warn("Invalid end date format: {}", endDate);
            }
        }

        wrapper.orderByDesc(MasseurEarning::getCreateTime);

        IPage<MasseurEarning> pageResult = masseurEarningMapper.selectPage(page, wrapper);
        return PageVO.of(pageResult);
    }
}
