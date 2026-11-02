package com.order.service.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.order.service.enums.StatusType;
import com.order.service.exception.dto.ErrorDto;

import reactor.core.publisher.Mono;

import static com.order.service.constants.Constants.CONCURRET_RQ_IN_PROGRESS;
import static com.order.service.constants.Constants.IDEMPOTENCY_KEY;
import static com.order.service.constants.Constants.IDEMPOTENCY_PREFIX;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class HttpIdempotencyFilter implements WebFilter {

    @Value("${spring.cache-ttl}")
    private Long cacheTtl;
    private final ReactiveStringRedisTemplate redisTemplate;

    public HttpIdempotencyFilter(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (!request.getMethod().matches("POST")) {
            return chain.filter(exchange);
        }

        String idempotencyKey = request.getHeaders().getFirst(IDEMPOTENCY_KEY);

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return chain.filter(exchange);
        }

        String redisKey = IDEMPOTENCY_PREFIX + idempotencyKey;

        return redisTemplate.opsForValue()
                .setIfAbsent(redisKey, StatusType.PROCESSING.toString(), Duration.ofMillis(cacheTtl))
                .flatMap(isNew -> {
                    if (Boolean.FALSE.equals(isNew)) {
                        return redisTemplate.opsForValue().get(redisKey)
                                .flatMap(cachedValue -> {
                                    ServerHttpResponse response = exchange.getResponse();
                                    String cleanValue = cachedValue != null ? cachedValue.replace("\u0000", "").trim()
                                            : "";

                                    if (cleanValue.startsWith(StatusType.PROCESSING.toString())) {
                                        response.setStatusCode(HttpStatus.CONFLICT);
                                        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                                        byte[] errorBytes = new ErrorDto(HttpStatus.CONFLICT,
                                                CONCURRET_RQ_IN_PROGRESS).toString()
                                                .getBytes(StandardCharsets.UTF_8);
                                        DataBuffer errorBuffer = response.bufferFactory().wrap(errorBytes);
                                        return response.writeWith(Mono.just(errorBuffer));
                                    }
                                    response.setStatusCode(HttpStatus.OK);
                                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                                    byte[] bytes = cleanValue.getBytes(StandardCharsets.UTF_8);
                                    DataBuffer buffer = response.bufferFactory().wrap(bytes);
                                    return response.writeWith(Mono.just(buffer));
                                });
                    }
                    ServerHttpResponse originalResponse = exchange.getResponse();
                    DataBufferFactory bufferFactory = originalResponse.bufferFactory();
                    ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                        @Override
                        public Mono<Void> writeWith(org.reactivestreams.Publisher<? extends DataBuffer> body) {
                            if (getStatusCode() != null && getStatusCode().is2xxSuccessful()) {
                                return org.springframework.core.io.buffer.DataBufferUtils.join(body)
                                        .flatMap(dataBuffer -> {
                                            byte[] content = new byte[dataBuffer.readableByteCount()];
                                            dataBuffer.read(content);
                                            org.springframework.core.io.buffer.DataBufferUtils.release(dataBuffer);
                                            String responseBody = new String(content, StandardCharsets.UTF_8);
                                            return redisTemplate.opsForValue()
                                                    .set(redisKey, responseBody, cacheTtl)
                                                    .then(super.writeWith(Mono.just(bufferFactory.wrap(content))));
                                        });
                            } else {
                                return redisTemplate.delete(redisKey)
                                        .then(super.writeWith(body));
                            }
                        }
                    };

                    return chain.filter(exchange.mutate().response(decoratedResponse).build())
                            .onErrorResume(ex -> redisTemplate.delete(redisKey).then(Mono.error(ex)));
                });
    }

}
