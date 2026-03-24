// package com.lovepreet.ratelimiter.service;

// import org.springframework.stereotype.Service;

// import com.lovepreet.ratelimiter.redis.RedisTokenBucketService;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class RateLimiterService {
//     private final RedisTokenBucketService redisTokenBucketService;

//     public boolean isAllowed(String clientId) {
//         return redisTokenBucketService.allowRequest(clientId);
//     }
// }
