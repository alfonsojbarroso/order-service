package com.order.service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.order.service.dto.NotificationDto;
import com.order.service.dto.OrderDto;
import com.order.service.notification.NotificationClient;
import com.order.service.util.DateTimeUtil;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationClient notificationClient;

    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @CircuitBreaker(name = "notificationService", fallbackMethod = "sendNotificationFallback")
    @Retry(name = "notificationServiceRetry")
    public void triggerNotificationForOrder(OrderDto order) {
        // Preparamos el DTO de la petición POST
        NotificationDto request = new NotificationDto(
                order.orderId().toString(),
                "Notification Service",
                "Tu orden ha sido creada exitosamente.",
                DateTimeUtil.getTimeStampInMilli());

        log.info("Enviando petición POST a notifications-service para la orden: {}", order.orderId().toString());

        // Llamada POST mediante Feign
        notificationClient.sendNotification(request);
    }

    // Método de respaldo (Fallback) en caso de que el circuito esté abierto o falle
    private void sendNotificationFallback(OrderDto order, Throwable t) {
        log.error("Fallo al contactar a notifications-service. Ejecutando fallback. Motivo: {}", t.getMessage());
    }
}
