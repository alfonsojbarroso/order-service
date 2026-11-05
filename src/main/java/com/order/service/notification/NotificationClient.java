package com.order.service.notification;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.order.service.dto.NotificationDto;

@FeignClient(name = "notification-service", url = "${url.notification-service}")
public interface NotificationClient {

    @PostMapping("/notification-service")
    void sendNotification(@RequestBody NotificationDto request);
}
