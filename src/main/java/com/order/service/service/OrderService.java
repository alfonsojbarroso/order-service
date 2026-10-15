package com.order.service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.order.service.dto.OrderDto;
import com.order.service.model.OrderModel;
import com.order.service.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducerService orderProducerService;

    public OrderService(OrderRepository orderRepository, OrderProducerService orderProducerService) {
        this.orderRepository = orderRepository;
        this.orderProducerService = orderProducerService;
    }

    public OrderDto save(OrderDto orderDto) {
        OrderDto producedOrderDto = orderProducerService.createOrder(orderDto);
        return orderRepository.save(orderDto.toModel()).toDto();
    }

    public List<OrderDto> fetchAll() {
        return orderRepository.findAll().stream().map(OrderModel::toDto).toList();
    }

    public OrderDto findById(String id) {
        return orderRepository.findById(id).get().toDto();
    }

}
