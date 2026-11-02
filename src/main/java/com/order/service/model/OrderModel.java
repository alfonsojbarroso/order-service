package com.order.service.model;

import java.util.UUID;

import com.order.service.dto.OrderDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import tools.jackson.databind.ObjectMapper;

@Entity
@Table(name = "orders")
public class OrderModel {

    @Id
    @Column(name = "order_id", columnDefinition = "uuid")
    private UUID orderId;
    @Column(name = "customer_id", columnDefinition = "int")
    private Integer customerId;
    @Column(name = "description", columnDefinition = "text")
    private String description;
    @Column(name = "status", columnDefinition = "char(15)")
    private String status;
    @Column(name = "created_at", columnDefinition = "bigint")
    private Long createdAt;

    public OrderDto toDto() {
        return new OrderDto(
                orderId,
                customerId,
                description,
                status,
                createdAt);
    }

    public OrderModel() {
    }

    public OrderModel(UUID orderId, Integer customerId, String description, String status, Long createdAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
    }

}