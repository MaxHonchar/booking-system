package com.test.booking.utils;

import com.test.booking.enums.AccommodationType;
import com.test.booking.enums.EventType;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

@Slf4j
public class CommonUtils {

    public static final double COEFFICIENT = 15.0;
    public static final int ONE_HUNDRED = 100;

    public static AccommodationType getAccommodationType(String accommodationType) {
        try {
            return AccommodationType.valueOf(accommodationType);
        } catch (Exception e) {
            log.warn("Error while getting accommodation type {}", accommodationType, e);
            return null;
        }
    }

    public static EventType getEventType(String eventType) {
        try {
            return EventType.valueOf(eventType);
        } catch (Exception e) {
            log.warn("Error while getting event type {}", eventType, e);
            return EventType.DELETE;
        }
    }

    public static BigDecimal getDecimal(Double cost) {
        return Optional.ofNullable(cost).map(BigDecimal::valueOf).orElse(null);
    }

    public static Optional<Instant> convertLocalDateToInstant(LocalDate localDate) {
        return Optional.ofNullable(localDate)
                .map(date -> date.atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    public static BigDecimal getCostWithBookingPercentage(Double value) {
        double percentage = COEFFICIENT / ONE_HUNDRED;
        return BigDecimal.valueOf(value + value * percentage);
    }

}
