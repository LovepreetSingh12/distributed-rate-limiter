package com.lovepreet.ratelimiter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepreet.ratelimiter.model.RateLimitConfig;

public interface RateLimitConfigRepository extends JpaRepository<RateLimitConfig, Long> {
    RateLimitConfig findByIpAddress(String ipAdress);
    RateLimitConfig findByIpAddressIsNull();
}
