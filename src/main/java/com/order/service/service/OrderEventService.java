package com.order.service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.order.service.dto.OrderRequestDto;

@Service
public class OrderEventService {

    private final KafkaTemplate<String, OrderRequestDto> kafkaTemplate;

    public OrderEventService(KafkaTemplate<String, OrderRequestDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderRequestDto createOrder(OrderRequestDto orderRequestDto) {
        kafkaTemplate.send("service-requested-topic", orderRequestDto);
        return orderRequestDto;
    }

}
