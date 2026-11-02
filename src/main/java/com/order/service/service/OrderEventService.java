package com.order.service.service;

import static com.order.service.constants.Constants.IDEMPOTENCY_KEY;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.order.service.dto.OrderDto;

@Service
public class OrderEventService {

    @Value("${app.kafka.topics.order-events}")
    private String kafkaTopic;
    private final KafkaTemplate<UUID, OrderDto> kafkaTemplate;

    public OrderEventService(KafkaTemplate<UUID, OrderDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderDto createOrder(String idempotencyKey, OrderDto orderResponseDto) {
        kafkaTemplate.send(createProducerRecord(idempotencyKey, orderResponseDto));
        return orderResponseDto;
    }

    private ProducerRecord<UUID, OrderDto> createProducerRecord(String idempotencyKey,
            OrderDto orderRequestDto) {

        ProducerRecord<UUID, OrderDto> record = new ProducerRecord<>(kafkaTopic,
                orderRequestDto.orderId(),
                orderRequestDto);
        record.headers().add(IDEMPOTENCY_KEY, idempotencyKey.getBytes(StandardCharsets.UTF_8));

        return record;

    }

}
