package com.order.service.model;

import java.time.LocalDateTime;

import com.order.service.dto.OrderRequestDto;
import com.order.service.dto.OrderResponseDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class OrderModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "customer_name", columnDefinition = "varchar(80)")
    private String customerName;
    @Column(name = "service_description", columnDefinition = "text")
    private String serviceDescription;
    @Column(name = "status", columnDefinition = "varchar(20)")
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public OrderModel() {
    }

    public OrderModel(String id, String customerName, String serviceDescription, String status,
            LocalDateTime createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.serviceDescription = serviceDescription;
        this.status = status;
        this.createdAt = createdAt;
    }

    public OrderResponseDto toRequestDto() {
        return new OrderResponseDto(
                this.id,
                this.customerName,
                this.serviceDescription,
                this.status,
                this.createdAt);
    }

}
