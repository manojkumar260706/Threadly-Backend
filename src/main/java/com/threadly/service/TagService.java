package com.threadly.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class TagService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Transactional(readOnly = true)
    public List<String> getTrendingTags(int limit) {
        Set<String> tags = stringRedisTemplate.opsForZSet()
                .reverseRange("trending:tags", 0, limit - 1);

        return tags == null ? List.of() : new ArrayList<>(tags);
    }
}
