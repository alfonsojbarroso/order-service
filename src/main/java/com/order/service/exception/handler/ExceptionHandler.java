package com.order.service.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.order.service.exception.BusinessException;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> businessExceptionHandler(BusinessException businessException) {
        return new ResponseEntity<>(businessException.getErrorResponseDto(),
                businessException.getErrorResponseDto().httpStatus());
    }

}
