package com.kaola.masseur.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.masseur.model.dto.MasseurDTO;
import com.kaola.masseur.model.entity.Masseur;
import com.kaola.masseur.model.vo.MasseurVO;
import com.kaola.masseur.mapper.MasseurMapper;
import com.kaola.masseur.service.MasseurService;
// TODO: Cross-service dependency - Project entity should be fetched via OpenFeign from kaola-project-service
// import com.kaola.project.model.entity.Project;
// import com.kaola.project.mapper.ProjectMapper;
// TODO: Cross-service dependency - LoginVO should be in common-core
// import com.kaola.common.core.vo.LoginVO;
// TODO: Cross-service dependency - TimeSlotVO should be in common-model
// import com.kaola.common.model.vo.TimeSlotVO;
// TODO: Cross-service dependency - JwtUtils should be in common-core or auth service
// import com.kaola.common.core.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 技师服务实现类 - 从数据库读取真实数据
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MasseurServiceImpl implements MasseurService {

    private final MasseurMapper masseurMapper;
    private final RestTemplate restTemplate;
    // TODO: Cross-service dependency - Replace with OpenFeign client to kaola-project-service
    // private final ProjectMapper projectMapper;
    // TODO: Cross-service dependency - JwtUtils should be provided by common-core or auth service
    // private final JwtUtils jwtUtils;

    // Product service URL
    private static final String PRODUCT_SERVICE_URL = "http://localhost:8085";

    // TODO: Cross-service dependency - Login should be handled by auth service
    // @Override
    // public LoginVO login(String code) {
    //     log.info("技师微信登录, code: {}", code);
    //
    //     // 开发环境：使用第一个技师作为登录用户
    //     LambdaQueryWrapper<Masseur> queryWrapper = new LambdaQueryWrapper<>();
    //     queryWrapper.eq(Masseur::getStatus, 1)
    //                 .eq(Masseur::getDeleted, 0)
    //                 .last("LIMIT 1");
    //     Masseur masseur = masseurMapper.selectOne(queryWrapper);
    //
    //     Long masseurId = masseur != null ? masseur.getId() : 1L;
    //
    //     // 生成真正的JWT token，使用MASSEUR角色
    //     String token = jwtUtils.generateToken(masseurId, java.util.Map.of("role", "MASSEUR"));
    //
    //     LoginVO loginVO = new LoginVO();
    //     loginVO.setToken(token);
    //     loginVO.setTokenType("Bearer");
    //     loginVO.setExpiresIn(86400L);
    //     loginVO.setIsNewUser(false);
    //     loginVO.setNeedBindPhone(false);
    //
    //     return loginVO;
    // }

    @Override
    public MasseurVO getMasseurInfo(Long masseurId) {
        log.info("获取技师信息, masseurId: {}", masseurId);

        Masseur masseur = masseurMapper.selectById(masseurId);
        if (masseur == null) {
            return null;
        }
        MasseurVO vo = convertToVO(masseur);

        // Fetch services from product service via HTTP
        try {
            List<MasseurVO.ServiceItemVO> services = getServicesForMasseur(masseurId);
            vo.setServices(services);
            vo.setServiceCount(services != null ? services.size() : 0);
        } catch (Exception e) {
            log.error("获取技师服务项目失败, masseurId: {}", masseurId, e);
            vo.setServices(new ArrayList<>());
            vo.setServiceCount(0);
        }

        return vo;
    }

    /**
     * 获取技师可提供的服务项目 - 通过HTTP调用product服务
     */
    private List<MasseurVO.ServiceItemVO> getServicesForMasseur(Long masseurId) {
        try {
            String url = PRODUCT_SERVICE_URL + "/project/list";
            log.info("调用产品服务获取项目列表: {}", url);

            // 调用product service API
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getBody() != null && response.getBody().get("data") != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> projects = (List<Map<String, Object>>) response.getBody().get("data");

                return projects.stream().map(project -> {
                    MasseurVO.ServiceItemVO item = new MasseurVO.ServiceItemVO();
                    item.setId(((Number) project.get("id")).longValue());
                    item.setName((String) project.get("name"));
                    item.setDescription((String) project.get("description"));

                    // Handle price conversion
                    Object priceObj = project.get("price");
                    if (priceObj instanceof Number) {
                        item.setPrice(new BigDecimal(priceObj.toString()));
                    }

                    // Handle duration conversion
                    Object durationObj = project.get("duration");
                    if (durationObj instanceof Number) {
                        item.setDuration(((Number) durationObj).intValue());
                    }

                    item.setSelected(false);
                    return item;
                }).collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("调用产品服务失败", e);
        }

        return new ArrayList<>();
    }

    @Override
    public boolean updateMasseurInfo(Long masseurId, MasseurDTO dto) {
        log.info("更新技师信息, masseurId: {}, dto: {}", masseurId, dto);
        return true;
    }

    @Cacheable(value = "masseurs", key = "'store:' + (#storeId != null ? #storeId : 'all')", unless = "#result == null || #result.isEmpty()")
    @Override
    public List<MasseurVO> getMasseursByStore(Long storeId) {
        log.info("获取门店技师列表 - 从数据库查询, storeId: {}", storeId);

        List<Masseur> masseurs;
        if (storeId != null) {
            // 根据门店ID查询技师
            masseurs = masseurMapper.findByStoreId(storeId);
        } else {
            // 查询所有可用技师（status=1, deleted=0）
            LambdaQueryWrapper<Masseur> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Masseur::getStatus, 1)
                        .eq(Masseur::getDeleted, 0)
                        .orderByDesc(Masseur::getRating);
            masseurs = masseurMapper.selectList(queryWrapper);
        }

        return masseurs.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<MasseurVO> getMasseursBySymptom(Long symptomId) {
        log.info("按症状获取技师, symptomId: {}", symptomId);

        if (symptomId == null) {
            return getMasseursByStore(null);
        }

        // 通过项目分类ID查询技师（JOIN masseur_project和t_project表）
        List<Masseur> masseurs = masseurMapper.findByCategoryId(symptomId);
        return masseurs.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<MasseurVO> getMasseursByStoreAndSymptom(Long storeId, Long symptomId) {
        log.info("根据门店和症状获取技师, storeId: {}, symptomId: {}", storeId, symptomId);

        if (storeId == null || symptomId == null) {
            // 如果任一参数为空，回退到单参数查询
            if (symptomId != null) {
                return getMasseursBySymptom(symptomId);
            } else {
                return getMasseursByStore(storeId);
            }
        }

        // 同时根据门店ID和项目分类ID查询技师
        List<Masseur> masseurs = masseurMapper.findByStoreIdAndCategoryId(storeId, symptomId);
        return masseurs.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    // TODO: Cross-service dependency - Scheduling should be handled by scheduling service
    // @Override
    // public List<TimeSlotVO> getAvailableTime(Long masseurId, LocalDate date) {
    //     log.info("获取技师可预约时间, masseurId: {}, date: {}", masseurId, date);
    //
    //     // 返回Mock时间段列表
    //     List<TimeSlotVO> timeSlots = new ArrayList<>();
    //
    //     LocalTime startTime = LocalTime.of(9, 0);
    //     for (int i = 0; i < 8; i++) {
    //         TimeSlotVO slot = new TimeSlotVO();
    //         slot.setScheduleId(1L);
    //         slot.setStartTime(startTime.plusHours(i));
    //         slot.setEndTime(startTime.plusHours(i + 1));
    //         slot.setTimeText(slot.getStartTime() + "-" + slot.getEndTime());
    //         slot.setStatus(i < 6 ? 1 : 2);
    //         slot.setStatusText(i < 6 ? "可预约" : "已预约");
    //         slot.setSelectable(i < 6);
    //         timeSlots.add(slot);
    //     }
    //
    //     return timeSlots;
    // }

    /**
     * 将实体转换为VO
     */
    private MasseurVO convertToVO(Masseur masseur) {
        MasseurVO vo = new MasseurVO();
        vo.setId(masseur.getId());
        vo.setStoreId(1L);  // 默认门店ID，后续可关联门店技师表
        vo.setName(masseur.getName());
        vo.setAvatar(masseur.getAvatar());
        vo.setGender(masseur.getGender());
        vo.setLevel(masseur.getLevel());
        vo.setLevelName(getLevelName(masseur.getLevel()));
        vo.setRating(masseur.getRating());
        vo.setReviewCount(0); // TODO: 需要从评价服务通过OpenFeign获取
        vo.setOrderCount(0);  // TODO: 需要从订单服务通过OpenFeign获取
        vo.setWorkYears(3);   // 默认值

        // 解析skills JSON数组
        String skills = masseur.getSkills();
        List<String> skillList = new ArrayList<>();
        if (skills != null && skills.startsWith("[")) {
            String cleanSkills = skills.replace("[", "").replace("]", "").replace("\"", "");
            vo.setTags(cleanSkills);
            // 解析为列表作为擅长症状
            for (String skill : cleanSkills.split(",")) {
                skillList.add(skill.trim());
            }
        } else {
            vo.setTags(skills);
        }
        vo.setSpecialties(skillList);

        // 设置个人简介和经验
        vo.setIntroduction("从业多年，精通多种推拿手法，擅长解决各类身体不适问题。");
        vo.setExperience(vo.getWorkYears() + "年");

        vo.setAvailable(masseur.getStatus() == 1);
        return vo;
    }

    /**
     * 获取等级名称
     */
    private String getLevelName(Integer level) {
        if (level == null) return "初级技师";
        switch (level) {
            case 1: return "初级技师";
            case 2: return "中级技师";
            case 3: return "高级技师";
            case 4: return "专家技师";
            default: return "初级技师";
        }
    }
}
