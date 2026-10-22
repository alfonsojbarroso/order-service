package com.order.service.service;

import static com.order.service.constants.Constants.HTTP_425_STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.order.service.dto.OrderRequestDto;
import com.order.service.dto.OrderResponseDto;
import com.order.service.enums.OrderStatus;
import com.order.service.exception.BusinessException;

class OrderServiceTest {

    private static final String IDEMPOTENCY_VALUE = "idempotency-123";

    private IdempotencyService idempotencyService;
    private OrderDataService orderDataService;
    private OrderEventService orderEventService;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        idempotencyService = mock(IdempotencyService.class);
        orderDataService = mock(OrderDataService.class);
        orderEventService = mock(OrderEventService.class);
        orderService = new OrderService(orderDataService, orderEventService, idempotencyService);
    }

    @Test
    void createOrderAcquiresLockPersistsOrderAndPublishesEvent() throws BusinessException {
        OrderRequestDto request = new OrderRequestDto(
                null, 42, "Alex Morgan", "Replace a screen", null, null);
        OrderResponseDto expectedResponse = new OrderResponseDto(
                "order-123", 42, "Alex Morgan", "Replace a screen", "PROCESSING", null);
        when(idempotencyService.acquireLock(IDEMPOTENCY_VALUE)).thenReturn(true);
        when(orderDataService.save(request)).thenReturn(expectedResponse);

        OrderResponseDto response = orderService.createOrder(IDEMPOTENCY_VALUE, request);

        assertSame(expectedResponse, response);
        verify(idempotencyService).acquireLock(IDEMPOTENCY_VALUE);
        verify(orderDataService).save(request);
        verify(orderEventService).createOrder(IDEMPOTENCY_VALUE, request);
    }

    @Test
    void createOrderRejectsRequestWhenExistingLockIsProcessing() {
        OrderRequestDto request = new OrderRequestDto(
                null, 42, "Alex Morgan", "Replace a screen", null, null);
        when(idempotencyService.acquireLock(IDEMPOTENCY_VALUE)).thenReturn(false);
        when(idempotencyService.getStoredResponse(IDEMPOTENCY_VALUE))
                .thenReturn(OrderStatus.PROCESSING.toString());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> orderService.createOrder(IDEMPOTENCY_VALUE, request));

        assertEquals(HTTP_425_STRING, exception.getMessage());
        assertEquals(HttpStatus.TOO_EARLY, exception.getErrorResponseDto().httpStatus());
        assertEquals(425, exception.getErrorResponseDto().statusCode());
        verify(orderDataService, never()).save(request);
        verify(orderEventService, never()).createOrder(IDEMPOTENCY_VALUE, request);
    }

    @Test
    void createOrderChecksStoredResponseWhenLockCannotBeAcquired() throws BusinessException {
        OrderRequestDto request = new OrderRequestDto(
                null, 42, "Alex Morgan", "Replace a screen", null, null);
        OrderResponseDto expectedResponse = new OrderResponseDto(
                "order-123", 42, "Alex Morgan", "Replace a screen", "PROCESSING", null);
        when(idempotencyService.acquireLock(IDEMPOTENCY_VALUE)).thenReturn(false);
        when(idempotencyService.getStoredResponse(IDEMPOTENCY_VALUE)).thenReturn(null);
        when(orderDataService.save(request)).thenReturn(expectedResponse);

        OrderResponseDto response = orderService.createOrder(IDEMPOTENCY_VALUE, request);

        assertSame(expectedResponse, response);
        verify(idempotencyService).getStoredResponse(IDEMPOTENCY_VALUE);
        verify(orderDataService).save(request);
        verify(orderEventService).createOrder(IDEMPOTENCY_VALUE, request);
    }

    @Test
    void fetchAllDelegatesToOrderDataService() {
        List<OrderResponseDto> expectedResponses = List.of(
                new OrderResponseDto("order-123", 42, "Alex Morgan", "Repair", "PROCESSING", null));
        when(orderDataService.fetchAll()).thenReturn(expectedResponses);

        List<OrderResponseDto> responses = orderService.fetchAll();

        assertSame(expectedResponses, responses);
        verify(orderDataService).fetchAll();
    }

    @Test
    void findByIdDelegatesToOrderDataService() {
        OrderResponseDto expectedResponse = new OrderResponseDto(
                "order-123", 42, "Alex Morgan", "Repair", "PROCESSING", null);
        when(orderDataService.findById("order-123")).thenReturn(expectedResponse);

        OrderResponseDto response = orderService.findById("order-123");

        assertSame(expectedResponse, response);
        verify(orderDataService).findById("order-123");
    }
}
