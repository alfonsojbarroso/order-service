package com.order.service.exception;

import com.order.service.dto.ErrorResponseDto;

public class BusinessException extends Exception {

    private final ErrorResponseDto errorResponseDto;

    public BusinessException(String message, ErrorResponseDto errorResponseDto) {
        super(message);
        this.errorResponseDto = errorResponseDto;
    }

    public ErrorResponseDto getErrorResponseDto() {
        return errorResponseDto;
    }

}
