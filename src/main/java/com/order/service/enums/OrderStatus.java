package com.order.service.enums;

public enum OrderStatus {
    IN_PROGRESS("IN PROGRESS"), COMPLETED("COMPLETED"), CANCELED("CANCELED");

    private final String description;

    private OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

}
