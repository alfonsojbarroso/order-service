package com.order.service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.order.service.dto.OrderDto;
import com.order.service.model.OrderModel;
import com.order.service.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderDto> fetchAll() {
        return orderRepository.findAll().stream().map(OrderModel::toDto).toList();
    }

}
