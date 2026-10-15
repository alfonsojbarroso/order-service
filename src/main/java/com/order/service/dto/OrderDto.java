package com.order.service.dto;

import java.time.LocalDateTime;

import com.order.service.model.OrderModel;

public record OrderDto(String id, String customerName, String serviceDescription, String status,
        LocalDateTime createdAt) {

    public OrderModel toModel() {
        return new OrderModel(id, customerName, serviceDescription, status, createdAt);
    }

}
