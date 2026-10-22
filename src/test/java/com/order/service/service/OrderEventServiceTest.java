package com.order.service.service;

import static com.order.service.constants.Constants.IDEMPOTENCY_KEY;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.order.service.dto.OrderRequestDto;

class OrderEventServiceTest {

    private static final String TOPIC = "service-requested-topic";
    private static final String IDEMPOTENCY_VALUE = "idempotency-123";

    private KafkaTemplate<String, OrderRequestDto> kafkaTemplate;
    private OrderEventService orderEventService;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mockKafkaTemplate();
        orderEventService = new OrderEventService(kafkaTemplate);
        ReflectionTestUtils.setField(orderEventService, "kafkaTopic", TOPIC);
    }

    @SuppressWarnings("unchecked")
    private static KafkaTemplate<String, OrderRequestDto> mockKafkaTemplate() {
        return mock(KafkaTemplate.class);
    }

    @Test
    void createOrderPublishesRequestAndReturnsIt() {
        OrderRequestDto request = new OrderRequestDto(
                null, 42, "Alex Morgan", "Replace a screen", null, null);

        OrderRequestDto result = orderEventService.createOrder(IDEMPOTENCY_VALUE, request);

        assertSame(request, result);

        ArgumentCaptor<ProducerRecord<String, OrderRequestDto>> recordCaptor = producerRecordCaptor();
        verify(kafkaTemplate).send(recordCaptor.capture());
        ProducerRecord<String, OrderRequestDto> record = recordCaptor.getValue();
        assertEquals(TOPIC, record.topic());
        assertEquals("42", record.key());
        assertSame(request, record.value());
        assertArrayEquals(
                IDEMPOTENCY_VALUE.getBytes(StandardCharsets.UTF_8),
                record.headers().lastHeader(IDEMPOTENCY_KEY).value());
    }

    @SuppressWarnings("unchecked")
    private static ArgumentCaptor<ProducerRecord<String, OrderRequestDto>> producerRecordCaptor() {
        return (ArgumentCaptor<ProducerRecord<String, OrderRequestDto>>) (ArgumentCaptor<?>)
                ArgumentCaptor.forClass(ProducerRecord.class);
    }
}
