package com.order.service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.order.service.dto.OrderDto;
import com.order.service.exception.BusinessException;

@Service
public class OrderService {

    private final OrderDataService orderDataService;
    private final OrderEventService orderEventService;

    public OrderService(OrderDataService orderDataService, OrderEventService orderEventService) {
        this.orderDataService = orderDataService;
        this.orderEventService = orderEventService;
    }

    public OrderDto createOrder(String idempotencyKey, OrderDto orderDto)
            throws BusinessException {
        orderDataService.save(orderDto);
        orderEventService.createOrder(idempotencyKey, orderDto);
        return orderDto;
    }

    public List<OrderDto> fetchAll() {
        return orderDataService.fetchAll();
    }

    public OrderDto findById(String id) {
        return orderDataService.findById(id);
    }

}
