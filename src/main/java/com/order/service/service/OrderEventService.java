package com.order.service.service;

import org.apache.kafka.clients.producer.ProducerRecord;
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
        kafkaTemplate.send(createProducerRecord(orderRequestDto));
        return orderRequestDto;
    }

    private ProducerRecord<String, OrderRequestDto> createProducerRecord(OrderRequestDto orderRequestDto) {
        return new ProducerRecord<>("service-requested-topic", orderRequestDto.customerId().toString(),
                orderRequestDto);
    }

}
