package com.order.service.dto;

import java.util.UUID;

public record BillingDto(
        UUID billingId,
        UUID orderId,
        Integer customerId,
        String description,
        String status,
        Long createdAt) {

}
