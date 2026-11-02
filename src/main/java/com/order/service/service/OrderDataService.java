package com.order.service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.order.service.dto.OrderDto;
import com.order.service.model.OrderModel;
import com.order.service.repository.OrderRepository;

@Service
public class OrderDataService {

    private final OrderRepository orderRepository;

    public OrderDataService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderDto save(OrderDto orderDto) {
        return orderRepository.save(orderDto.toModel()).toDto();
    }

    public List<OrderDto> fetchAll() {
        return orderRepository.findAll().stream().map(OrderModel::toDto).toList();
    }

    public OrderDto findById(String id) {
        return orderRepository.findById(id).get().toDto();
    }

}
