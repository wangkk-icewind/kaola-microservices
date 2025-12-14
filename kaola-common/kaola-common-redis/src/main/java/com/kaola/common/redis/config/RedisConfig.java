package com.kaola.common.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis配置类
 * 配置序列化方式、缓存管理器
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Redis模板配置
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用Jackson2JsonRedisSerializer序列化Value
        GenericJackson2JsonRedisSerializer jsonSerializer = createJsonSerializer();

        // Key使用String序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 设置Key的序列化方式
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 设置Value的序列化方式
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 缓存管理器配置
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJackson2JsonRedisSerializer jsonSerializer = createJsonSerializer();

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 默认缓存过期时间：1小时
                .entryTtl(Duration.ofHours(1))
                // Key前缀
                .prefixCacheNameWith("kaola:")
                // 禁止缓存null值
                .disableCachingNullValues()
                // Key序列化
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // Value序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                // 为不同缓存配置不同过期时间
                .withCacheConfiguration("userCache",
                    config.entryTtl(Duration.ofHours(2)))
                .withCacheConfiguration("smsCode",
                    config.entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration("serviceList",
                    config.entryTtl(Duration.ofHours(6)))
                .withCacheConfiguration("hotServices",
                    config.entryTtl(Duration.ofMinutes(30)))
                .build();
    }

    /**
     * 创建JSON序列化器
     */
    private GenericJackson2JsonRedisSerializer createJsonSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        // 支持Java 8日期时间
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
