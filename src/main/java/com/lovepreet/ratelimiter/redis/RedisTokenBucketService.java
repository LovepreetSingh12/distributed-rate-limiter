// package com.lovepreet.ratelimiter.redis;

// import java.util.HashMap;
// import java.util.Map;

// import com.lovepreet.ratelimiter.repository.RateLimitConfigRepository;
// import com.lovepreet.ratelimiter.model.RateLimitConfig;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.stereotype.Service;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class RedisTokenBucketService {
    
//     private final RedisTemplate<String, Object> redisTemplate;
//     private final RateLimitConfigRepository rateLimitConfigRepository;

//     private static final int DEFAULT_CAPACITY = 10;
//     private static final int DEFAULT_REFILL_RATE = 1;

//     public boolean allowRequest(String clientId) {

//         RateLimitConfig config = rateLimitConfigRepository.findByClientId(clientId).orElse(null);
//         int capacity = config != null ? config.getCapacity() : DEFAULT_CAPACITY;
//         int refillRate = config != null ? config.getRefillRate() : DEFAULT_REFILL_RATE;

//         String key = "rate_limit:" + clientId;

//         Map<Object, Object> bucket = redisTemplate.opsForHash().entries(key);

//         long currentTime = System.currentTimeMillis();

//         int tokens;
//         long lastRefillTime;

//         if(bucket.isEmpty()) {
//             tokens = capacity;
//             lastRefillTime = currentTime;
//         } else {
//             tokens = (int) bucket.getOrDefault("tokens", 0);
//             lastRefillTime = (long) bucket.getOrDefault("lastRefillTime", currentTime);
//         }

//         long elapsed = (currentTime - lastRefillTime)/1000;

//         int refill = (int) (elapsed * refillRate);
//         tokens = Math.min(capacity, tokens + refill);

//         if (tokens <= 0) {
//             return false;
//         }

//         tokens--;

//         Map<String, Object> newBucket = new HashMap<>();
//         newBucket.put("tokens", tokens);
//         newBucket.put("lastRefill", currentTime);

//         redisTemplate.opsForHash().putAll(key, newBucket);

//         return true;

//     }

// }
