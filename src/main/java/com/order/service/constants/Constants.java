package com.order.service.constants;

public class Constants {

    public static final String HTTP_425_STRING = "Request in progress. Please try again.";
    public static final String IDEMPOTENCY_KEY = "idempotency-key";
    public static final String IDEMPOTENCY_PREFIX = "idempotency:";
    public static final String IDEMPOTENCY_HEADER_ERROR = "Idempotency header is not valid";
    public static final String CUSTOMER_NAME_ERROR = "Customer name is not valid";
    public static final String CUSTOMER_ID_ERROR = "Customer id is not valid";
    public static final String DESCRIPTION_ERROR = "Description is not valid";
    public static final String INVALID_INPUT = "Invalid input";

    private Constants() {
    }
}
