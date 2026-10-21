package com.order.service.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.order.service.dto.OrderRequestDto;
import com.order.service.dto.OrderResponseDto;
import com.order.service.service.OrderDataService;

@RestController
@RequestMapping("${url.order}")
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final OrderDataService orderDataService;

    public OrderController(OrderDataService orderDataService) {
        this.orderDataService = orderDataService;
    }

    @PostMapping
    @Operation(summary = "Create an order", description = "Saves a new order and returns the created order.")
    public ResponseEntity<?> save(@RequestBody OrderRequestDto orderRequestDto) {
        OrderResponseDto dto = orderDataService.save(orderRequestDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Returns all orders.")
    public ResponseEntity<?> findAll() {
        List<OrderResponseDto> list = orderDataService.fetchAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/by-id")
    @Operation(summary = "Get an order by ID", description = "Returns the order matching the supplied ID.")
    public ResponseEntity<?> findById(@RequestParam(name = "id") String id) {
        OrderResponseDto dto = orderDataService.findById(id.trim());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}
