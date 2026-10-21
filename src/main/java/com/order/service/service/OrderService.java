package com.order.service.service;

import static com.order.service.constants.Constants.HTTP_425_STRING;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.order.service.dto.ErrorResponseDto;
import com.order.service.dto.OrderRequestDto;
import com.order.service.dto.OrderResponseDto;
import com.order.service.enums.OrderStatus;
import com.order.service.exception.BusinessException;

@Service
public class OrderService {

    private final IdempotencyService idempotencyService;
    private final OrderDataService orderDataService;
    private final OrderEventService orderEventService;

    public OrderService(OrderDataService orderDataService, OrderEventService orderEventService,
            IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
        this.orderDataService = orderDataService;
        this.orderEventService = orderEventService;
    }

    public OrderResponseDto createOrder(String idempotencyKey, OrderRequestDto orderRequestDto)
            throws BusinessException {

        boolean acquired = idempotencyService.acquireLock(idempotencyKey);
        if (!acquired) {
            String cachedResponse = idempotencyService.getStoredResponse(idempotencyKey);
            if (OrderStatus.PROCESSING.toString().equals(cachedResponse)) {
                ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.TOO_EARLY, HTTP_425_STRING);
                throw new BusinessException(HTTP_425_STRING, errorResponseDto);
            }
        }

        OrderResponseDto orderResponseDto = orderDataService.save(orderRequestDto);
        orderEventService.createOrder(orderRequestDto);
        return orderResponseDto;
    }

    public List<OrderResponseDto> fetchAll() {
        return orderDataService.fetchAll();
    }

    public OrderResponseDto findById(String id) {
        return orderDataService.findById(id);
    }

}
