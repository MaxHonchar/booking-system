package com.test.booking.dtos;

import com.test.booking.enums.AccommodationType;
import com.test.booking.enums.EventType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class UnitSearchDto {
    private AccommodationType accommodationType;
    private EventType eventType = EventType.DELETE;
    private BigDecimal minCost;
    private BigDecimal maxCost;
    private Instant fromDate;
    private Instant toDate;
}
