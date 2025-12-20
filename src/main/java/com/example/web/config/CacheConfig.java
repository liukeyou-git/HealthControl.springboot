package com.example.web.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;
import java.util.Arrays;

/**
 * 缓存配置类
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 配置缓存管理器
     * 使用Caffeine作为内存缓存实现，支持TTL
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // 配置缓存策略：1小时过期，最大1000个条目
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS) // 1小时后过期
                .maximumSize(1000) // 最大缓存条目数
                .recordStats()); // 启用统计

        // 设置缓存名称
        cacheManager.setCacheNames(Arrays.asList("aiHealthAnalysis"));
        return cacheManager;
    }
}