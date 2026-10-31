package com.order.service.service;

import static com.order.service.constants.Constants.IDEMPOTENCY_KEY;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.order.service.dto.OrderResponseDto;

@Service
public class OrderEventService {

    @Value("${app.kafka.topics.order-events}")
    private String kafkaTopic;
    private final KafkaTemplate<String, OrderResponseDto> kafkaTemplate;

    public OrderEventService(KafkaTemplate<String, OrderResponseDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderResponseDto createOrder(String idempotencyKey, OrderResponseDto orderResponseDto) {
        kafkaTemplate.send(createProducerRecord(idempotencyKey, orderResponseDto));
        return orderResponseDto;
    }

    private ProducerRecord<String, OrderResponseDto> createProducerRecord(String idempotencyKey,
            OrderResponseDto orderRequestDto) {

        ProducerRecord<String, OrderResponseDto> record = new ProducerRecord<>(kafkaTopic,
                orderRequestDto.id(),
                orderRequestDto);
        record.headers().add(IDEMPOTENCY_KEY, idempotencyKey.getBytes(StandardCharsets.UTF_8));

        return record;

    }

}
