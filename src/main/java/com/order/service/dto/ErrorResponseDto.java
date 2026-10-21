package com.order.service.dto;

import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

public record ErrorResponseDto(@JsonIgnore HttpStatus httpStatus, Integer statusCode, String message) {

    public ErrorResponseDto(HttpStatus httpStatus, String message) {
        this(httpStatus, httpStatus.value(), message);
    }

}
