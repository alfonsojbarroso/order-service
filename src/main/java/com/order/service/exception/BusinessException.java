package com.order.service.exception;

import com.order.service.exception.dto.ErrorDto;

public class BusinessException extends Exception {

    private final ErrorDto errorResponseDto;

    public BusinessException(String message, ErrorDto errorResponseDto) {
        super(message);
        this.errorResponseDto = errorResponseDto;
    }

    public ErrorDto getErrorResponseDto() {
        return errorResponseDto;
    }

}
