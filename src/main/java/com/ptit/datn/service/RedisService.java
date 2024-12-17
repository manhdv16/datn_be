package com.ptit.datn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String key, HashMap<String, String> data, long time) {
        redisTemplate.opsForHash().putAll(key, data);
        redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
    }

    public void save(String key, String value, long time) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
    }

    public String findByKey(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            return value.toString();
        }
        return "";
    }
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public HashMap<String, String> findHashByKey(String key) {
        return (HashMap) redisTemplate.opsForHash().entries(key);
    }
}
