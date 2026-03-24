package com.lovepreet.ratelimiter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "rate_limit_config")
public class RateLimitConfig {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ipAddress;
    private int capacity;
    private int refillRate;
    private int refillInterval;
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public int getRefillRate() {
        return refillRate;
    }
    public void setRefillRate(int refillRate) {
        this.refillRate = refillRate;
    }
    public int getRefillInterval() {
        return refillInterval;
    }
    public void setRefillInterval(int refillInterval) {
        this.refillInterval = refillInterval;
    }
}
