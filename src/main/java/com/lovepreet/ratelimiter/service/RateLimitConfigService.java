package com.lovepreet.ratelimiter.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.lovepreet.ratelimiter.model.RateLimitConfig;
import com.lovepreet.ratelimiter.model.RateLimitConfigRequest;
import com.lovepreet.ratelimiter.model.RateLimitViolation;
import com.lovepreet.ratelimiter.repository.RateLimitConfigRepository;
import com.lovepreet.ratelimiter.repository.RateLimitViolationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimitConfigService {


    private final RateLimitConfigRepository configRepo;
    private final RateLimitViolationRepository violationRepo;
    private final StringRedisTemplate redisTemplate;
    private final CacheManager cacheManager;


    @Value("${rate-limiter.capacity}") private int defaultCapacity;
    @Value("${rate-limiter.refill-rate}") private int defaultRefillRate;
    @Value("${rate-limiter.refill-interval}") private int defaultRefillInterval;

    @Cacheable(value = "rateConfig", key = "#ip")
    public RateLimitConfig getConfigForIp(String ip) {
        var config = configRepo.findByIpAddress(ip);
        if(config != null) {
            return config;
        }
        return defaultConfig();
    }

    private RateLimitConfig defaultConfig() {
        RateLimitConfig c = new RateLimitConfig();
        c.setCapacity(defaultCapacity);
        c.setRefillRate(defaultRefillRate);
        c.setRefillInterval(defaultRefillInterval);
        return c;
    }

    @CacheEvict(value = "rateConfig", key = "#request.ipAddress")
    public RateLimitConfig upsertConfig(RateLimitConfigRequest request) {
        RateLimitConfig config = configRepo.findByIpAddress(request.getIpAddress());
        if (config == null) {
            config = new RateLimitConfig();
        }
        config.setIpAddress(request.getIpAddress());
        config.setCapacity(request.getCapacity());
        config.setRefillRate(request.getRefillRate());
        config.setRefillInterval(request.getRefillInterval());
        return configRepo.save(config);
    }

    @CacheEvict(value = "rateConfig", key = "#ip")
    public void deleteConfig(String ip) {
        var config = configRepo.findByIpAddress(ip);
        if (config != null) {
            configRepo.delete(config);
        }
    }

    public void resetTokensForIp(String ip) {
        redisTemplate.delete("rate_limit:" + ip);
        // Evict cache too
        Cache cache = cacheManager.getCache("rateConfig");
        if (cache != null) cache.evict(ip);
    }

    public Page<RateLimitViolation> getViolations(String ip, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("violatedAt").descending());
        if (ip != null && !ip.isBlank()) {
            return violationRepo.findByIpAddress(ip, pageable);
        }
        return violationRepo.findAll(pageable);
    }

    public ViolationStats getViolationStats(int hours) {
        LocalDateTime from = LocalDateTime.now().minusHours(hours);
        return violationRepo.getViolationStats(from);
    }

    public List<TopIpStat> getTopOffendingIps(int limit) {
        return violationRepo.findTopOffendingIps(PageRequest.of(0, limit));
    }
}