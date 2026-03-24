package com.lovepreet.ratelimiter.model;

import lombok.Data;

@Data
public class RateLimitConfigRequest {

    private String ipAddress;
    private int capacity;
    private int refillRate;
    private int refillInterval;
}