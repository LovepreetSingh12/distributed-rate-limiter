package com.lovepreet.ratelimiter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lovepreet.ratelimiter.model.RateLimitViolation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RateLimitViolationRepository extends JpaRepository<RateLimitViolation, Long> {

    Page<RateLimitViolation> findByIpAddress(String ip, Pageable pageable);

    @Query("""
        SELECT new com.example.ratelimiter.dto.TopIpStat(v.ipAddress, COUNT(v))
        FROM RateLimitViolation v
        GROUP BY v.ipAddress
        ORDER BY COUNT(v) DESC
        """)
    List<TopIpStat> findTopOffendingIps(Pageable pageable);

    @Query("""
        SELECT new com.example.ratelimiter.dto.ViolationStats(
            COUNT(v),
            MIN(v.violatedAt),
            MAX(v.violatedAt)
        )
        FROM RateLimitViolation v
        WHERE v.violatedAt >= :from
        """)
    ViolationStats getViolationStats(@Param("from") LocalDateTime from);

    @Query("""
        SELECT DATE_TRUNC('hour', v.violatedAt) as hour, COUNT(v) as count
        FROM RateLimitViolation v
        WHERE v.violatedAt >= :from
        GROUP BY DATE_TRUNC('hour', v.violatedAt)
        ORDER BY hour ASC
        """)
    List<Object[]> getViolationsGroupedByHour(@Param("from") LocalDateTime from);
}