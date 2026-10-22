package com.order.service.dto;

import static com.order.service.constants.Constants.IDEMPOTENCY_HEADER_ERROR;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HeaderDto(
        @NotNull(message = IDEMPOTENCY_HEADER_ERROR) @NotBlank(message = IDEMPOTENCY_HEADER_ERROR) String idempotencyKey) {
}
