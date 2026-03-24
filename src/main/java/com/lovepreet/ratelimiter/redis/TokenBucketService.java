package com.lovepreet.ratelimiter.redis;

import java.time.Instant;
import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import com.lovepreet.ratelimiter.model.RateLimitConfig;
import com.lovepreet.ratelimiter.model.TokenBucketResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenBucketService {

    private final StringRedisTemplate redisTemplate;

    // Lua script: atomic token bucket check-and-consume
    private static final String TOKEN_BUCKET_SCRIPT = """
        local key = KEYS[1]
        local capacity = tonumber(ARGV[1])
        local refill_rate = tonumber(ARGV[2])
        local refill_interval = tonumber(ARGV[3])
        local now = tonumber(ARGV[4])

        local bucket = redis.call('HMGET', key, 'tokens', 'last_refill')
        local tokens = tonumber(bucket[1]) or capacity
        local last_refill = tonumber(bucket[2]) or now

        -- Calculate tokens to add based on elapsed time
        local elapsed = now - last_refill
        local intervals = math.floor(elapsed / refill_interval)
        tokens = math.min(capacity, tokens + intervals * refill_rate)

        if intervals > 0 then
            last_refill = last_refill + intervals * refill_interval
        end

        if tokens > 0 then
            tokens = tokens - 1
            redis.call('HMSET', key, 'tokens', tokens, 'last_refill', last_refill)
            redis.call('EXPIRE', key, refill_interval * 2)
            return {1, tokens}   -- allowed, remaining tokens
        else
            redis.call('HMSET', key, 'tokens', tokens, 'last_refill', last_refill)
            return {0, 0}        -- denied
        end
        """;

    public TokenBucketResult tryConsume(String ip, RateLimitConfig config) {
        RedisScript<List> script = RedisScript.of(TOKEN_BUCKET_SCRIPT, List.class);
        long now = Instant.now().getEpochSecond();

        List<Long> result = redisTemplate.execute(
            script,
            List.of("rate_limit:" + ip),
            String.valueOf(config.getCapacity()),
            String.valueOf(config.getRefillRate()),
            String.valueOf(config.getRefillInterval()),
            String.valueOf(now)
        );

        boolean allowed = result.get(0) == 1L;
        int remaining = result.get(1).intValue();
        return new TokenBucketResult(allowed, remaining);
    }
}

