package com.order.service.service;

import static com.order.service.constants.Constants.IDEMPOTENCY_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class IdempotencyServiceTest {

    private static final String IDEMPOTENCY_KEY = "test-key";
    private static final String REDIS_KEY = IDEMPOTENCY_PREFIX + IDEMPOTENCY_KEY;

    private StringRedisTemplate stringRedisTemplate;
    private ValueOperations<String, String> valueOperations;
    private IdempotencyService idempotencyService;

    @BeforeEach
    void setUp() {
        stringRedisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mockValueOperations();
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        idempotencyService = new IdempotencyService(stringRedisTemplate);
    }

    @SuppressWarnings("unchecked")
    private static ValueOperations<String, String> mockValueOperations() {
        return mock(ValueOperations.class);
    }

    @Test
    void acquireLockReturnsTrueWhenLockIsAcquired() {
        when(valueOperations.setIfAbsent(REDIS_KEY, "PROCESSING", Duration.ofHours(24)))
                .thenReturn(true);

        boolean acquired = idempotencyService.acquireLock(IDEMPOTENCY_KEY);

        assertTrue(acquired);
        verify(valueOperations).setIfAbsent(REDIS_KEY, "PROCESSING", Duration.ofHours(24));
    }

    @Test
    void acquireLockReturnsFalseWhenLockAlreadyExists() {
        when(valueOperations.setIfAbsent(REDIS_KEY, "PROCESSING", Duration.ofHours(24)))
                .thenReturn(false);

        boolean acquired = idempotencyService.acquireLock(IDEMPOTENCY_KEY);

        assertFalse(acquired);
        verify(valueOperations).setIfAbsent(REDIS_KEY, "PROCESSING", Duration.ofHours(24));
    }

    @Test
    void acquireLockReturnsFalseWhenRedisReturnsNull() {
        when(valueOperations.setIfAbsent(REDIS_KEY, "PROCESSING", Duration.ofHours(24)))
                .thenReturn(null);

        boolean acquired = idempotencyService.acquireLock(IDEMPOTENCY_KEY);

        assertFalse(acquired);
        verify(valueOperations).setIfAbsent(REDIS_KEY, "PROCESSING", Duration.ofHours(24));
    }

    @Test
    void saveResponseStoresJsonResponseWithExpiration() {
        String jsonResponse = "{\"id\":\"order-123\"}";

        idempotencyService.saveResponse(IDEMPOTENCY_KEY, jsonResponse);

        verify(valueOperations).set(REDIS_KEY, jsonResponse, Duration.ofHours(24));
    }

    @Test
    void getStoredResponseReturnsResponseFromRedis() {
        String jsonResponse = "{\"id\":\"order-123\"}";
        when(valueOperations.get(REDIS_KEY)).thenReturn(jsonResponse);

        String storedResponse = idempotencyService.getStoredResponse(IDEMPOTENCY_KEY);

        assertEquals(jsonResponse, storedResponse);
        verify(valueOperations).get(REDIS_KEY);
    }

    @Test
    void getStoredResponseReturnsNullWhenNoResponseIsStored() {
        when(valueOperations.get(REDIS_KEY)).thenReturn(null);

        String storedResponse = idempotencyService.getStoredResponse(IDEMPOTENCY_KEY);

        assertNull(storedResponse);
        verify(valueOperations).get(REDIS_KEY);
    }
}
