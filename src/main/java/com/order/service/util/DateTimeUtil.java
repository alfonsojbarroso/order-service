package com.order.service.util;

import java.time.Instant;

public final class DateTimeUtil {

    public static Long getTimeStampInMilli() {
        return Instant.now().toEpochMilli();
    }

    private DateTimeUtil() {
    }

}
