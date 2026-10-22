package com.order.service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.order.service.dto.ErrorResponseDto;
import com.order.service.dto.HeaderDto;
import com.order.service.dto.OrderRequestDto;
import com.order.service.dto.OrderResponseDto;
import com.order.service.exception.BusinessException;
import com.order.service.service.OrderService;
import com.order.service.util.ValidateUtil;

class OrderControllerTest {

    private OrderService orderService;
    private ValidateUtil validateUtil;
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        validateUtil = mock(ValidateUtil.class);
        orderController = new OrderController(orderService, validateUtil);
    }

    @Test
    void saveValidatesRequestCreatesOrderAndReturnsOk() throws BusinessException {
        HeaderDto header = new HeaderDto("idempotency-123");
        OrderRequestDto request = new OrderRequestDto(
                null, 42, "Alex Morgan", "Replace a screen", null, null);
        OrderResponseDto expectedResponse = new OrderResponseDto(
                "order-123", 42, "Alex Morgan", "Replace a screen", "PROCESSING", null);
        when(orderService.createOrder("idempotency-123", request)).thenReturn(expectedResponse);

        ResponseEntity<?> response = orderController.save(header, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expectedResponse, response.getBody());
        verify(validateUtil).validatePost(header, request);
        verify(orderService).createOrder("idempotency-123", request);
    }

    @Test
    void savePropagatesValidationFailureWithoutCreatingOrder() throws BusinessException {
        HeaderDto header = new HeaderDto("invalid-key");
        OrderRequestDto request = new OrderRequestDto(
                null, 42, "Alex Morgan", "Replace a screen", null, null);
        BusinessException validationException = new BusinessException(
                "Invalid input", new ErrorResponseDto(HttpStatus.BAD_REQUEST, "Invalid input"));
        doThrow(validationException).when(validateUtil).validatePost(header, request);

        BusinessException thrown = assertThrows(
                BusinessException.class,
                () -> orderController.save(header, request));

        assertSame(validationException, thrown);
        verify(orderService, never()).createOrder("invalid-key", request);
    }

    @Test
    void findAllReturnsOrdersWithOkStatus() {
        List<OrderResponseDto> expectedOrders = List.of(
                new OrderResponseDto("order-123", 42, "Alex Morgan", "Repair", "PROCESSING", null));
        when(orderService.fetchAll()).thenReturn(expectedOrders);

        ResponseEntity<?> response = orderController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expectedOrders, response.getBody());
        verify(orderService).fetchAll();
    }

    @Test
    void findByIdTrimsIdAndReturnsOrderWithOkStatus() {
        OrderResponseDto expectedOrder = new OrderResponseDto(
                "order-123", 42, "Alex Morgan", "Repair", "PROCESSING", null);
        when(orderService.findById("order-123")).thenReturn(expectedOrder);

        ResponseEntity<?> response = orderController.findById("  order-123  ");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expectedOrder, response.getBody());
        verify(orderService).findById("order-123");
    }
}
