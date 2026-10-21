package com.order.service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.order.service.dto.OrderRequestDto;
import com.order.service.dto.OrderResponseDto;
import com.order.service.model.OrderModel;
import com.order.service.repository.OrderRepository;

@Service
public class OrderDataService {

    private final OrderRepository orderRepository;

    public OrderDataService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponseDto save(OrderRequestDto orderRequestDto) {
        return orderRepository.save(orderRequestDto.toModel()).toRequestDto();
    }

    public List<OrderResponseDto> fetchAll() {
        return orderRepository.findAll().stream().map(OrderModel::toRequestDto).toList();
    }

    public OrderResponseDto findById(String id) {
        return orderRepository.findById(id).get().toRequestDto();
    }

}
