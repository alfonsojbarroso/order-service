package com.order.service.dto;

import java.time.LocalDateTime;

import com.order.service.enums.OrderStatus;
import com.order.service.model.OrderModel;

public record OrderResponseDto(
        String id,
        String customerName,
        String serviceDescription,
        String status,
        LocalDateTime createdAt) {

    public OrderResponseDto {
        status = OrderStatus.IN_PROGRESS.getDescription();
        createdAt = LocalDateTime.now();
    }

    public OrderModel toModel() {
        return new OrderModel(id, customerName, serviceDescription, status, createdAt);
    }

}
