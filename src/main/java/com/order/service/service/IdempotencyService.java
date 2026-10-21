package com.order.service.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {

    private final StringRedisTemplate stringRedisTemplate;

    public IdempotencyService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public boolean acquireLock(String idempotencyKey) {
        String redisKey = "idempotency:" + idempotencyKey;
        Boolean isNew = stringRedisTemplate.opsForValue()
                .setIfAbsent(redisKey, "PROCESSING", Duration.ofHours(24));
        return Boolean.TRUE.equals(isNew);
    }

    public void saveResponse(String idempotencyKey, String jsonResponse) {
        String redisKey = "idempotency:" + idempotencyKey;
        // Sobrescribimos el valor con la respuesta exitosa manteniendo el TTL
        stringRedisTemplate.opsForValue().set(redisKey, jsonResponse, Duration.ofHours(24));
    }

    /**
     * Obtiene la respuesta guardada previamente.
     */
    public String getStoredResponse(String idempotencyKey) {
        String redisKey = "idempotency:" + idempotencyKey;
        return stringRedisTemplate.opsForValue().get(redisKey);
    }

}
