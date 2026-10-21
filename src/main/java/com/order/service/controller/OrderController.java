package com.order.service.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.order.service.dto.OrderRequestDto;
import com.order.service.dto.OrderResponseDto;
import com.order.service.service.IdempotencyService;
import com.order.service.service.OrderService;

@RestController
@RequestMapping("${url.order}")
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final IdempotencyService idempotencyService;
    private final OrderService orderService;

    public OrderController(OrderService orderService, IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create an order", description = "Saves a new order and returns the created order.")
    public ResponseEntity<?> save(@RequestHeader("idempotency-key") String idempotencyKey,
            @RequestBody OrderRequestDto orderRequestDto) {

        boolean acquired = idempotencyService.acquireLock(idempotencyKey);
        if (!acquired) {
            String cachedResponse = idempotencyService.getStoredResponse(idempotencyKey);

            if ("PROCESSING".equals(cachedResponse)) {
                return ResponseEntity.status(425).body("{\"error\": \"Petición en proceso. Intente de nuevo.\" }");
            }
            return ResponseEntity.ok(cachedResponse);
        }
        OrderResponseDto dto = orderService.createOrder(orderRequestDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Returns all orders.")
    public ResponseEntity<?> findAll() {
        List<OrderResponseDto> list = orderService.fetchAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/by-id")
    @Operation(summary = "Get an order by ID", description = "Returns the order matching the supplied ID.")
    public ResponseEntity<?> findById(@RequestParam(name = "id") String id) {
        OrderResponseDto dto = orderService.findById(id.trim());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}
