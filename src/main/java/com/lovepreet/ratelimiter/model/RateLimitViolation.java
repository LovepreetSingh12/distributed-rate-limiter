package com.lovepreet.ratelimiter.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "rate_limit_violations")
@Data @Builder
public class RateLimitViolation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ipAddress;
    private String requestPath;
    private LocalDateTime violatedAt;
    private int tokensAtViolation;
}