package com.order.service.util;

import static com.order.service.constants.Constants.INVALID_INPUT;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.order.service.dto.ErrorResponseDto;
import com.order.service.dto.HeaderDto;
import com.order.service.dto.OrderRequestDto;
import com.order.service.exception.BusinessException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Component
public class ValidateUtil {

    private ErrorResponseDto errorResponseDto;
    private final Validator validator;

    public ValidateUtil(Validator validator) {
        this.validator = validator;
    }

    public void validatePost(HeaderDto idempotencyKey, OrderRequestDto orderRequestDto) throws BusinessException {
        violationCheck(idempotencyKey);
        violationCheck(orderRequestDto);
    }

    private <T> void violationCheck(T obj) throws BusinessException {
        if (obj == null) {
            errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST,
                    INVALID_INPUT);
            throw new BusinessException(INVALID_INPUT, errorResponseDto);
        }
        Set<ConstraintViolation<T>> violations = validator.validate(obj);
        if (!violations.isEmpty()) {
            String message = violations.stream().findFirst().get().getMessage();
            errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST,
                    message);
            throw new BusinessException(message, errorResponseDto);
        }
    }

}
