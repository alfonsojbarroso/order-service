package com.order.service.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.order.service.enums.OrderStatus;

@Service
public class IdempotencyService {

    private final StringRedisTemplate stringRedisTemplate;

    public IdempotencyService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public boolean acquireLock(String idempotencyKey) {
        String redisKey = "idempotency:" + idempotencyKey;
        Boolean isNew = stringRedisTemplate.opsForValue()
                .setIfAbsent(redisKey, OrderStatus.PROCESSING.toString(), Duration.ofHours(24));
        return Boolean.TRUE.equals(isNew);
    }

    public void saveResponse(String idempotencyKey, String jsonResponse) {
        String redisKey = "idempotency:" + idempotencyKey;
        stringRedisTemplate.opsForValue().set(redisKey, jsonResponse, Duration.ofHours(24));
    }

    public String getStoredResponse(String idempotencyKey) {
        String redisKey = "idempotency:" + idempotencyKey;
        return stringRedisTemplate.opsForValue().get(redisKey);
    }

}
