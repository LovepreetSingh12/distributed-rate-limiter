package com.lovepreet.ratelimiter.kafka;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.lovepreet.ratelimiter.model.RateLimitEvent;
import com.lovepreet.ratelimiter.model.RateLimitViolation;
import com.lovepreet.ratelimiter.repository.RateLimitViolationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimitEventConsumer {
    private final RateLimitViolationRepository violationRepository;
    private Logger logger = Logger.getLogger(getClass().getName());

    @KafkaListener(topics = "rate_limit_events", groupId = "rate-limiter-audit")
    public void consume(RateLimitEvent event) {
        try {
            if(!event.isAllowed()) {
                violationRepository.save(RateLimitViolation.builder()
                    .ipAddress(event.getIpAddress())
                    .requestPath(event.getRequestPath())
                    .violatedAt(LocalDateTime.now())
                    .tokensAtViolation(event.getRemainingTokens())
                    .build());
            }
        } catch (Exception e) {
            logger.info("Failed to process rate limit event: " + e.getMessage());
        }
        
    }
}
