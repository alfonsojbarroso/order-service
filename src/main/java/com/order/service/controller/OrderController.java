package com.order.service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.order.service.dto.OrderDto;
import com.order.service.service.OrderService;

@RestController
@RequestMapping("${url.order}")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<OrderDto> list = orderService.fetchAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}
