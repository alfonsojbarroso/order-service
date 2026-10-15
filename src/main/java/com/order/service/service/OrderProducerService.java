package com.order.service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.order.service.dto.OrderDto;

@Service
public class OrderProducerService {

    private final KafkaTemplate<String, OrderDto> kafkaTemplate;

    public OrderProducerService(KafkaTemplate<String, OrderDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderDto createOrder(OrderDto orderDto) {
        kafkaTemplate.send("service-requested-topic", orderDto);
        return orderDto;
    }

}
