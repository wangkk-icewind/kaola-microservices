package com.kaola.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaola.common.core.dto.Result;
import com.kaola.order.client.MasseurServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 技师信息缓存服务
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MasseurCacheService {

    private final MasseurServiceClient masseurServiceClient;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_KEY_PREFIX = "masseur:info:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    /**
     * 获取技师信息（带缓存）
     *
     * @param masseurId 技师ID
     * @return 技师信息Map
     */
    public Map<String, Object> getMasseurInfo(Long masseurId) {
        String cacheKey = CACHE_KEY_PREFIX + masseurId;

        try {
            // 1. 尝试从Redis缓存获取
            String cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                log.debug("从Redis缓存获取技师信息: masseurId={}", masseurId);
                return objectMapper.readValue(cachedData, Map.class);
            }

            // 2. 缓存未命中，调用Feign客户端获取
            log.debug("缓存未命中，调用技师服务: masseurId={}", masseurId);
            Result<MasseurServiceClient.MasseurVO> result = masseurServiceClient.getMasseurDetail(masseurId);

            if (result != null && result.getCode() == 0 && result.getData() != null) {
                MasseurServiceClient.MasseurVO vo = result.getData();
                Map<String, Object> masseurInfo = convertToMap(vo);

                // 3. 将结果写入Redis缓存
                String jsonData = objectMapper.writeValueAsString(masseurInfo);
                redisTemplate.opsForValue().set(cacheKey, jsonData, CACHE_TTL);
                log.debug("技师信息已缓存: masseurId={}, ttl={}分钟", masseurId, CACHE_TTL.toMinutes());

                return masseurInfo;
            } else {
                // 4. Feign调用成功但返回错误，从缓存获取历史数据
                log.warn("技师服务返回错误，尝试从缓存获取历史数据: masseurId={}", masseurId);
                return getFromCacheOrDefault(cacheKey, masseurId);
            }

        } catch (Exception e) {
            // 5. 发生异常（Feign降级或其他错误），从缓存获取历史数据
            log.error("获取技师信息失败，尝试从缓存获取历史数据: masseurId={}", masseurId, e);
            return getFromCacheOrDefault(cacheKey, masseurId);
        }
    }

    /**
     * 从缓存获取数据，如果缓存也没有则返回默认数据
     */
    private Map<String, Object> getFromCacheOrDefault(String cacheKey, Long masseurId) {
        try {
            // 尝试从缓存获取历史数据（忽略TTL）
            String cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                log.info("使用Redis缓存的历史数据: masseurId={}", masseurId);
                return objectMapper.readValue(cachedData, Map.class);
            }
        } catch (Exception ex) {
            log.error("从缓存读取历史数据失败: masseurId={}", masseurId, ex);
        }

        // 如果缓存也没有，返回默认数据
        log.warn("无法获取技师信息，使用默认数据: masseurId={}", masseurId);
        return getDefaultMasseurInfo(masseurId);
    }

    /**
     * 获取默认技师信息（降级数据）
     */
    private Map<String, Object> getDefaultMasseurInfo(Long masseurId) {
        Map<String, Object> defaultInfo = new HashMap<>();
        defaultInfo.put("id", masseurId);
        defaultInfo.put("name", "技师" + masseurId + "号");
        defaultInfo.put("avatar", "https://example.com/avatar/default.jpg");
        defaultInfo.put("level", 2); // 降级时使用中级倍率（避免计价偏低）
        defaultInfo.put("status", 1);
        return defaultInfo;
    }

    /**
     * 转换VO为Map
     */
    private Map<String, Object> convertToMap(MasseurServiceClient.MasseurVO vo) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", vo.getId());
        map.put("name", vo.getName());
        map.put("avatar", vo.getAvatar());
        map.put("phone", vo.getPhone());
        map.put("gender", vo.getGender());
        map.put("age", vo.getAge());
        map.put("introduction", vo.getIntroduction());
        map.put("specialty", vo.getSpecialty());
        map.put("level", vo.getLevel());
        map.put("rating", vo.getRating());
        map.put("serviceCount", vo.getServiceCount());
        map.put("storeId", vo.getStoreId());
        map.put("status", vo.getStatus());
        return map;
    }

    /**
     * 清除技师信息缓存
     *
     * @param masseurId 技师ID
     */
    public void evictCache(Long masseurId) {
        String cacheKey = CACHE_KEY_PREFIX + masseurId;
        redisTemplate.delete(cacheKey);
        log.info("技师信息缓存已清除: masseurId={}", masseurId);
    }
}
