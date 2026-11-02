package com.order.service.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.order.service.dto.BillingDto;
import com.order.service.exception.BusinessException;

@Component
public class BillingConsumer {

    private final OrderDataService dataService;

    public BillingConsumer(OrderDataService dataService) {
        this.dataService = dataService;
    }

    @KafkaListener(topics = "${app.kafka.topics.billing-events}", groupId = "billing-service-group")
    public void confirmOrder(BillingDto billingDto) throws BusinessException {
        dataService.updateOrder(billingDto);
    }

}
