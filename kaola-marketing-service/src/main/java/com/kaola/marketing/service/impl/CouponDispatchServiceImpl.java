package com.kaola.marketing.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.marketing.mapper.CouponDispatchMapper;
import com.kaola.marketing.mapper.UserCouponMapper;
import com.kaola.marketing.model.dto.DispatchRequest;
import com.kaola.marketing.model.entity.CouponDispatch;
import com.kaola.marketing.model.entity.UserCoupon;
import com.kaola.marketing.model.vo.DispatchPreviewVO;
import com.kaola.marketing.service.CouponDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponDispatchServiceImpl implements CouponDispatchService {

    private final CouponDispatchMapper dispatchMapper;
    private final UserCouponMapper userCouponMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public DispatchPreviewVO preview(DispatchRequest request) {
        List<Map<String, Object>> users = queryMatchingUsers(request);

        DispatchPreviewVO vo = new DispatchPreviewVO();
        vo.setTargetCount(users.size());

        List<String> samples = users.stream()
            .limit(5)
            .map(u -> maskPhone((String) u.get("phone")))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        vo.setSamplePhones(samples);
        return vo;
    }

    @Override
    @Transactional
    public Long execute(DispatchRequest request, String operator) {
        List<Map<String, Object>> users = queryMatchingUsers(request);

        CouponDispatch dispatch = new CouponDispatch();
        dispatch.setCouponId(request.getCouponId());
        dispatch.setDispatchName(request.getDispatchName());
        dispatch.setRules(JSON.toJSONString(request.getRules()));
        dispatch.setPhoneList(request.getPhoneList() != null ? String.join(",", request.getPhoneList()) : null);
        dispatch.setTargetCount(users.size());
        dispatch.setSentCount(0);
        dispatch.setStatus(1);
        dispatch.setSmsNotify(Boolean.TRUE.equals(request.getSmsNotify()) ? 1 : 0);
        dispatch.setOperator(operator);
        dispatch.setRemark(request.getRemark());
        dispatch.setCreateTime(LocalDateTime.now());
        dispatch.setUpdateTime(LocalDateTime.now());
        dispatch.setDeleted(0);
        dispatchMapper.insert(dispatch);

        int sentCount = 0;
        for (Map<String, Object> user : users) {
            Long userId = ((Number) user.get("id")).longValue();
            try {
                // Check if user already has this coupon (未使用=0)
                LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<UserCoupon>()
                    .eq(UserCoupon::getUserId, userId)
                    .eq(UserCoupon::getCouponId, request.getCouponId())
                    .eq(UserCoupon::getStatus, 0); // 未使用
                if (userCouponMapper.selectCount(wrapper) == 0) {
                    UserCoupon uc = new UserCoupon();
                    uc.setUserId(userId);
                    uc.setCouponId(request.getCouponId());
                    uc.setStatus(0); // 未使用
                    userCouponMapper.insert(uc);
                    sentCount++;

                    // SMS notification (mock - just log)
                    if (Boolean.TRUE.equals(request.getSmsNotify()) && user.get("phone") != null) {
                        log.info("[SMS Mock] 发送优惠券通知到: {}", user.get("phone"));
                    }
                }
            } catch (Exception e) {
                log.error("发送优惠券给用户{}失败", userId, e);
            }
        }

        dispatch.setSentCount(sentCount);
        dispatch.setStatus(2);
        dispatch.setUpdateTime(LocalDateTime.now());
        dispatchMapper.updateById(dispatch);

        log.info("优惠券发送完成, dispatchId: {}, 目标: {}, 实发: {}", dispatch.getId(), users.size(), sentCount);
        return dispatch.getId();
    }

    @Override
    public List<CouponDispatch> listByPage(int page, int size) {
        return dispatchMapper.selectList(
            new LambdaQueryWrapper<CouponDispatch>()
                .eq(CouponDispatch::getDeleted, 0)
                .orderByDesc(CouponDispatch::getCreateTime)
        );
    }

    private List<Map<String, Object>> queryMatchingUsers(DispatchRequest request) {
        // If phone list specified, query by phones directly
        if (request.getPhoneList() != null && !request.getPhoneList().isEmpty()) {
            String placeholders = request.getPhoneList().stream().map(p -> "?").collect(Collectors.joining(","));
            String sql = "SELECT id, phone, level, point FROM t_user WHERE phone IN (" + placeholders + ") AND status = 1 AND deleted = 0";
            return jdbcTemplate.queryForList(sql, request.getPhoneList().toArray());
        }

        // Build query from rules
        StringBuilder sql = new StringBuilder("SELECT u.id, u.phone, u.level, u.point FROM t_user u WHERE u.status = 1 AND u.deleted = 0");
        List<Object> params = new ArrayList<>();

        if (request.getRules() != null) {
            for (DispatchRequest.DispatchRule rule : request.getRules()) {
                switch (rule.getField()) {
                    case "totalSpend":
                        sql.append(" AND (SELECT COALESCE(SUM(o.total_amount),0) FROM t_order o WHERE o.user_id=u.id AND o.status=4 AND o.deleted=0)");
                        appendOperator(sql, params, rule.getOperator(), new java.math.BigDecimal(rule.getValue()));
                        break;
                    case "orderCount":
                        sql.append(" AND (SELECT COUNT(*) FROM t_order o WHERE o.user_id=u.id AND o.status=4 AND o.deleted=0)");
                        appendOperator(sql, params, rule.getOperator(), Integer.parseInt(rule.getValue()));
                        break;
                    case "level":
                        sql.append(" AND u.level");
                        appendOperator(sql, params, rule.getOperator(), Integer.parseInt(rule.getValue()));
                        break;
                    case "customerType":
                        if ("new".equals(rule.getValue())) {
                            sql.append(" AND (SELECT COUNT(*) FROM t_order o WHERE o.user_id=u.id AND o.status=4 AND o.deleted=0) = 0");
                        } else if ("returning".equals(rule.getValue())) {
                            sql.append(" AND (SELECT COUNT(*) FROM t_order o WHERE o.user_id=u.id AND o.status=4 AND o.deleted=0) > 0");
                        }
                        break;
                    case "storeId":
                        sql.append(" AND (SELECT COUNT(*) FROM t_order o WHERE o.user_id=u.id AND o.store_id=? AND o.status=4 AND o.deleted=0) > 0");
                        params.add(Long.parseLong(rule.getValue()));
                        break;
                    default:
                        log.warn("未知的派发规则字段: {}", rule.getField());
                }
            }
        }

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    private void appendOperator(StringBuilder sql, List<Object> params, String operator, Object value) {
        switch (operator) {
            case "gte": sql.append(" >= ?"); break;
            case "lte": sql.append(" <= ?"); break;
            case "eq": sql.append(" = ?"); break;
            default: sql.append(" >= ?");
        }
        params.add(value);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
