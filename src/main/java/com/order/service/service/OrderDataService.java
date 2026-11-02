package com.order.service.service;

import static com.order.service.constants.Constants.RECORD_NOT_FOUND;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.order.service.dto.BillingDto;
import com.order.service.dto.OrderDto;
import com.order.service.enums.StatusType;
import com.order.service.exception.BusinessException;
import com.order.service.exception.dto.ErrorDto;
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

    public void updateOrder(BillingDto billingDto) throws BusinessException {
        OrderModel orderModel = orderRepository.findById(billingDto.orderId())
                .orElseThrow(() -> new BusinessException(new ErrorDto(HttpStatus.NOT_FOUND, RECORD_NOT_FOUND)));
        if (orderModel.getStatus().trim().equalsIgnoreCase(StatusType.PROCESSING.toString())) {
            orderModel.setStatus(StatusType.COMPLETED.toString());
            orderRepository.save(orderModel);
        }
    }

    public List<OrderDto> fetchAll() {
        return orderRepository.findAll().stream().map(OrderModel::toDto).toList();
    }

    public OrderDto findById(UUID id) {
        return orderRepository.findById(id).get().toDto();
    }

}
