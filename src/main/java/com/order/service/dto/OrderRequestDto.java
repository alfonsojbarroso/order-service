package com.order.service.dto;

import static com.order.service.constants.Constants.CUSTOMER_ID_ERROR;
import static com.order.service.constants.Constants.CUSTOMER_NAME_ERROR;
import static com.order.service.constants.Constants.DESCRIPTION_ERROR;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.order.service.enums.OrderStatus;
import com.order.service.model.OrderModel;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDto(
        @JsonIgnore String id,
        @NotNull(message = CUSTOMER_ID_ERROR) @Min(value = 0, message = CUSTOMER_ID_ERROR) Integer customerId,
        @NotBlank(message = CUSTOMER_NAME_ERROR) String customerName,
        @NotBlank(message = DESCRIPTION_ERROR) String serviceDescription,
        @JsonIgnore String status,
        @JsonIgnore LocalDateTime createdAt) {

    public OrderRequestDto {
        status = OrderStatus.PROCESSING.toString();
        createdAt = LocalDateTime.now();
    }

    public OrderModel toModel() {
        return new OrderModel(id, customerId, customerName, serviceDescription, status, createdAt);
    }

}
