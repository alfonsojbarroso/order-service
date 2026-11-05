package com.order.service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.order.service.dto.OrderDto;
import com.order.service.exception.BusinessException;
import com.order.service.messaging.OrderEventProducer;

@Service
public class OrderService {

    private final OrderDataService orderDataService;
    private final OrderEventProducer orderEventProducer;
    private final NotificationService notificationService;

    public OrderService(OrderDataService orderDataService, OrderEventProducer orderEventService,
            NotificationService notificationService) {
        this.orderDataService = orderDataService;
        this.orderEventProducer = orderEventService;
        this.notificationService = notificationService;
    }

    public OrderDto createOrder(String idempotencyKey, OrderDto orderDto)
            throws BusinessException {
        orderDataService.save(orderDto);
        orderEventProducer.produceOrder(idempotencyKey, orderDto);
        notificationService.triggerNotificationForOrder(orderDto);
        return orderDto;
    }

    public List<OrderDto> fetchAll() {
        return orderDataService.fetchAll();
    }

    public OrderDto findById(UUID id) {
        return orderDataService.findById(id);
    }

}
