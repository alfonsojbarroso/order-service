package com.order.service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.order.service.model.OrderModel;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, UUID> {

}