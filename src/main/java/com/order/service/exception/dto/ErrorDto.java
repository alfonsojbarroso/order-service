package com.order.service.exception.dto;

import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import tools.jackson.databind.ObjectMapper;

public record ErrorDto(@JsonIgnore HttpStatus httpStatus, Integer statusCode, String message, Long timeStamp) {

    public ErrorDto(HttpStatus httpStatus, String message) {
        this(httpStatus, httpStatus.value(), message, System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
    }

}
