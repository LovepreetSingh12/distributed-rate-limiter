package com.lovepreet.ratelimiter.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.lovepreet.ratelimiter.model.RateLimitEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimitEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "rate_limit_events";

    public void publish(RateLimitEvent event) {
        kafkaTemplate.send(TOPIC, event.getIpAddress(), event.toString());
    }
}
