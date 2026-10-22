package com.order.service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.order.service.dto.OrderRequestDto;
import com.order.service.dto.OrderResponseDto;
import com.order.service.model.OrderModel;
import com.order.service.repository.OrderRepository;

class OrderDataServiceTest {

    private OrderRepository orderRepository;
    private OrderDataService orderDataService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderDataService = new OrderDataService(orderRepository);
    }

    @Test
    void savePersistsRequestModelAndReturnsSavedOrder() {
        OrderRequestDto request = new OrderRequestDto(
                null, 42, "Alex Morgan", "Replace a screen", null, null);
        OrderModel savedModel = new OrderModel(
                "order-123", 42, "Alex Morgan", "Replace a screen", "PROCESSING", LocalDateTime.now());
        when(orderRepository.save(any(OrderModel.class))).thenReturn(savedModel);

        OrderResponseDto response = orderDataService.save(request);

        ArgumentCaptor<OrderModel> modelCaptor = ArgumentCaptor.forClass(OrderModel.class);
        verify(orderRepository).save(modelCaptor.capture());
        OrderResponseDto persistedRequest = modelCaptor.getValue().toRequestDto();
        assertEquals(42, persistedRequest.customerId());
        assertEquals("Alex Morgan", persistedRequest.customerName());
        assertEquals("Replace a screen", persistedRequest.serviceDescription());
        assertEquals("order-123", response.id());
        assertEquals(42, response.customerId());
        assertEquals("Alex Morgan", response.customerName());
        assertEquals("Replace a screen", response.serviceDescription());
    }

    @Test
    void fetchAllMapsRepositoryResultsToResponses() {
        when(orderRepository.findAll()).thenReturn(List.of(
                new OrderModel("order-1", 1, "Alex", "Repair", "PROCESSING", LocalDateTime.now()),
                new OrderModel("order-2", 2, "Sam", "Installation", "COMPLETED", LocalDateTime.now())));

        List<OrderResponseDto> responses = orderDataService.fetchAll();

        assertEquals(2, responses.size());
        assertEquals("order-1", responses.get(0).id());
        assertEquals(1, responses.get(0).customerId());
        assertEquals("order-2", responses.get(1).id());
        assertEquals(2, responses.get(1).customerId());
        verify(orderRepository).findAll();
    }

    @Test
    void fetchAllReturnsEmptyListWhenRepositoryHasNoOrders() {
        when(orderRepository.findAll()).thenReturn(List.of());

        List<OrderResponseDto> responses = orderDataService.fetchAll();

        assertEquals(List.of(), responses);
        verify(orderRepository).findAll();
    }

    @Test
    void findByIdMapsMatchingRepositoryResultToResponse() {
        when(orderRepository.findById("order-123")).thenReturn(Optional.of(
                new OrderModel("order-123", 42, "Alex Morgan", "Replace a screen", "PROCESSING",
                        LocalDateTime.now())));

        OrderResponseDto response = orderDataService.findById("order-123");

        assertEquals("order-123", response.id());
        assertEquals(42, response.customerId());
        assertEquals("Alex Morgan", response.customerName());
        verify(orderRepository).findById("order-123");
    }

    @Test
    void findByIdThrowsWhenOrderDoesNotExist() {
        when(orderRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> orderDataService.findById("missing"));

        verify(orderRepository).findById("missing");
    }
}
