package com.lovepreet.ratelimiter.filter;

import java.util.logging.Logger;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lovepreet.ratelimiter.kafka.RateLimitEventProducer;
import com.lovepreet.ratelimiter.model.RateLimitConfig;
import com.lovepreet.ratelimiter.model.RateLimitEvent;
import com.lovepreet.ratelimiter.redis.TokenBucketService;
import com.lovepreet.ratelimiter.service.RateLimitConfigService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Order(1)
public class RateLimiterFilter  extends OncePerRequestFilter {
    private final TokenBucketService tokenBucketService;
    private final RateLimitConfigService configService;

    private final RateLimitEventProducer eventProducer;
    private Logger logger = Logger.getLogger(getClass().getName());

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        try {
            String ip = extractIp(request);
            RateLimitConfig config = configService.getConfigForIp(ip);
            var result = tokenBucketService.tryConsume(ip, config);

            eventProducer.publish(RateLimitEvent.builder()
                .ipAddress(ip)
                .requestPath(request.getRequestURI())
                .allowed(result.isAllowed())
                .remainingTokens(result.getRemainingTokens())
                .timestamp(System.currentTimeMillis())
                .build());
            
            response.setHeader("X-RateLimit-Remaining", String.valueOf(result.getRemainingTokens()));
            response.setHeader("X-RateLimit-Limit", String.valueOf(config.getCapacity()));

            if (!result.isAllowed()) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("{\"error\": \"Rate limit exceeded. Try again later.\"}");
                return;
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.info("Error in rate limiter filter: " + e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") ||   // health checks
            path.startsWith("/public") ||     // public endpoints
            path.startsWith("/swagger-ui") || // API docs
            path.startsWith("/v3/api-docs");  // OpenAPI spec
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null) ? forwarded.split(",")[0].trim()
                                   : request.getRemoteAddr();
    }
}
