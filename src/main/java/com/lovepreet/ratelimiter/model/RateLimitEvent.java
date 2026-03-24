package com.lovepreet.ratelimiter.model;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class RateLimitEvent {
    private String ipAddress;
    private String requestPath;
    private boolean allowed;
    private int remainingTokens;
    private long timestamp;
}
