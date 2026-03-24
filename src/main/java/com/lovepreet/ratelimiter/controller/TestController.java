// package com.lovepreet.ratelimiter.controller;

// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.lovepreet.ratelimiter.service.RateLimiterService;

// import lombok.RequiredArgsConstructor;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestHeader;


// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/api")
// public class TestController {
    
//     private final RateLimiterService rateLimiterService;

//     @GetMapping("test")
//     public ResponseEntity<String> test(@RequestHeader("client-id") String clientId) {
//         boolean allowed = rateLimiterService.isAllowed(clientId);
//         if(!allowed) {
//             return ResponseEntity.status(429).body("Too many requests");
//         }
//         return ResponseEntity.ok("Request successful");
//     }
    
// }
