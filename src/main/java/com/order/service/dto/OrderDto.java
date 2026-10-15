package com.order.service.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.order.service.model.OrderModel;

public record OrderDto(
        @JsonIgnore String id,
        String customerName,
        String serviceDescription,
        String status,
        @JsonIgnore LocalDateTime createdAt) {

    public OrderDto {
        createdAt = LocalDateTime.now();
    }

    public OrderModel toModel() {
        return new OrderModel(id, customerName, serviceDescription, status, createdAt);
    }

}
