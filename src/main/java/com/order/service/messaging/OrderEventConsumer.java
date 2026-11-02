package com.order.service.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.order.service.dto.BillingDto;
import com.order.service.exception.BusinessException;
import com.order.service.service.OrderDataService;

@Component
public class OrderEventConsumer {

    private final OrderDataService dataService;

    public OrderEventConsumer(OrderDataService dataService) {
        this.dataService = dataService;
    }

    @KafkaListener(topics = "${app.kafka.topics.billing-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void confirmOrder(BillingDto billingDto) throws BusinessException {
        dataService.updateOrder(billingDto);
    }

}
