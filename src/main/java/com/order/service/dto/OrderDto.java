package com.order.service.dto;

import static com.order.service.constants.Constants.CUSTOMER_ID_ERROR;
import static com.order.service.constants.Constants.DESCRIPTION_ERROR;

import java.util.UUID;

import com.order.service.enums.StatusType;
import com.order.service.model.OrderModel;

import static com.order.service.util.DateTimeUtil.getTimeStampInMilli;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.ObjectMapper;

public record OrderDto(
        UUID orderId,
        @NotNull(message = CUSTOMER_ID_ERROR) @Min(value = 1, message = CUSTOMER_ID_ERROR) Integer customerId,
        @NotBlank(message = DESCRIPTION_ERROR) String description,
        String status,
        Long createdAt) {

    public OrderDto {
        orderId = UUID.randomUUID();
        status = StatusType.PROCESSING.toString();
        createdAt = getTimeStampInMilli();
    }

    public OrderModel toModel() {
        return new OrderModel(
                orderId,
                customerId,
                description,
                status,
                createdAt);
    }

    @Override
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
    }

}