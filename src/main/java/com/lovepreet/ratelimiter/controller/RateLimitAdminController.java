package com.lovepreet.ratelimiter.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lovepreet.ratelimiter.model.*;
import com.lovepreet.ratelimiter.service.RateLimitConfigService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/rate-limit")
@RequiredArgsConstructor
public class RateLimitAdminController {

    private final RateLimitConfigService configService;

    // GET all configs
    @GetMapping("/configs")
    public ResponseEntity<List<RateLimitConfig>> getAllConfigs() {
        return ResponseEntity.ok(configService.getAllConfigs());
    }

    // GET config for a specific IP
    @GetMapping("/configs/{ip}")
    public ResponseEntity<RateLimitConfig> getConfigByIp(@PathVariable String ip) {
        return ResponseEntity.ok(configService.getConfigForIp(ip));
    }

    // CREATE or UPDATE config for an IP
    @PostMapping("/configs")
    public ResponseEntity<RateLimitConfig> upsertConfig(@RequestBody @Valid RateLimitConfigRequest request) {
        return ResponseEntity.ok(configService.upsertConfig(request));
    }

    // DELETE config for an IP (falls back to default)
    @DeleteMapping("/configs/{ip}")
    public ResponseEntity<Void> deleteConfig(@PathVariable String ip) {
        configService.deleteConfig(ip);
        return ResponseEntity.noContent().build();
    }

    // GET violations (paginated, filterable by IP)
    @GetMapping("/violations")
    public ResponseEntity<Page<RateLimitViolation>> getViolations(
            @RequestParam(required = false) String ip,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(configService.getViolations(ip, page, size));
    }

    // GET violation stats — for charts
    @GetMapping("/violations/stats")
    public ResponseEntity<ViolationStats> getStats(
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(configService.getViolationStats(hours));
    }

    // GET top offending IPs
    @GetMapping("/violations/top-ips")
    public ResponseEntity<List<TopIpStat>> getTopIps(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(configService.getTopOffendingIps(limit));
    }

    // RESET tokens for an IP in Redis (unblock them)
    @PostMapping("/configs/{ip}/reset")
    public ResponseEntity<Void> resetTokens(@PathVariable String ip) {
        configService.resetTokensForIp(ip);
        return ResponseEntity.ok().build();
    }
}
