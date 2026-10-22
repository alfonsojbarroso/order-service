package com.order.service.service;

import static com.order.service.constants.Constants.IDEMPOTENCY_KEY;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.order.service.dto.OrderRequestDto;

@Service
public class OrderEventService {

    @Value("${spring.kafka.consumer.topic}")
    private String kafkaTopic;
    private final KafkaTemplate<String, OrderRequestDto> kafkaTemplate;

    public OrderEventService(KafkaTemplate<String, OrderRequestDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderRequestDto createOrder(String idempotencyKey, OrderRequestDto orderRequestDto) {
        kafkaTemplate.send(createProducerRecord(idempotencyKey, orderRequestDto));
        return orderRequestDto;
    }

    private ProducerRecord<String, OrderRequestDto> createProducerRecord(String idempotencyKey,
            OrderRequestDto orderRequestDto) {

        ProducerRecord<String, OrderRequestDto> record = new ProducerRecord<>(kafkaTopic,
                orderRequestDto.customerId().toString(),
                orderRequestDto);
        record.headers().add(IDEMPOTENCY_KEY, idempotencyKey.getBytes(StandardCharsets.UTF_8));

        return record;

    }

}
