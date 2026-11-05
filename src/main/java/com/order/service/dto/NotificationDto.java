package com.order.service.dto;

import tools.jackson.databind.ObjectMapper;

public record NotificationDto(
        String notificationId,
        String subject,
        String message,
        Long timeStamp) {

    @Override
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
    }

}
