package com.order.service.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.order.service.enums.OrderStatus;
import com.order.service.model.OrderModel;

public record OrderRequestDto(
        @JsonIgnore String id,
        String customerName,
        String serviceDescription,
        @JsonIgnore String status,
        @JsonIgnore LocalDateTime createdAt) {

    public OrderRequestDto {
        status = OrderStatus.IN_PROGRESS.getDescription();
        createdAt = LocalDateTime.now();
    }

    public OrderModel toModel() {
        return new OrderModel(id, customerName, serviceDescription, status, createdAt);
    }

}
