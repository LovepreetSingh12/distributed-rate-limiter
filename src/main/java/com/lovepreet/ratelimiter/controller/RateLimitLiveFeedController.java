package com.lovepreet.ratelimiter.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.lovepreet.ratelimiter.model.RateLimitEvent;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/rate-limit")
@RequiredArgsConstructor
public class RateLimitLiveFeedController {

    // Thread-safe list of active SSE emitters
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping(value = "/live-feed", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter liveFeed() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }

    // Called from RateLimitFilter on every request
    public void broadcast(RateLimitEvent event) {
        List<SseEmitter> dead = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                    .name("rate-limit")
                    .data(event));
            } catch (IOException e) {
                dead.add(emitter);
            }
        }
        emitters.removeAll(dead);
    }
}