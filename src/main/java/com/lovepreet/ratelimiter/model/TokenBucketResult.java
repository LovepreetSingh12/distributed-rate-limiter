package com.lovepreet.ratelimiter.model;

import lombok.Value;

@Value
public class TokenBucketResult {
    boolean allowed;
    int remainingTokens;
}
