package com.order.service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.order.service.dto.OrderRequestDto;
import com.order.service.dto.OrderResponseDto;
import com.order.service.exception.BusinessException;

@Service
public class OrderService {

    private final OrderDataService orderDataService;
    private final OrderEventService orderEventService;

    public OrderService(OrderDataService orderDataService, OrderEventService orderEventService) {
        this.orderDataService = orderDataService;
        this.orderEventService = orderEventService;
    }

    public OrderResponseDto createOrder(String idempotencyKey, OrderRequestDto orderRequestDto)
            throws BusinessException {

        OrderResponseDto orderResponseDto = orderDataService.save(orderRequestDto);
        orderEventService.createOrder(idempotencyKey, orderResponseDto);
        return orderResponseDto;
    }

    public List<OrderResponseDto> fetchAll() {
        return orderDataService.fetchAll();
    }

    public OrderResponseDto findById(String id) {
        return orderDataService.findById(id);
    }

}
