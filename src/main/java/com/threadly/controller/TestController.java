package com.threadly.controller;

import com.threadly.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/")
    public String home() {
        return "Backend is running 🚀";
    }

    @GetMapping("/redis-check")
    public String check() {
        redisTemplate.opsForValue().set("hello", "code-general");
        return redisTemplate.opsForValue().get("hello");
    }

    @GetMapping("/db-test")
    public String dbTest() {
        return "Users count: " + userRepository.count();
    }


}
