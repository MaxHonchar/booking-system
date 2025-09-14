package com.test.booking.dtos;

import com.test.booking.enums.AccommodationType;
import com.test.booking.enums.BookingStatus;
import com.test.booking.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnitSearchDto {
    private AccommodationType accommodationType;
    private EventType eventType;
    private BigDecimal minCost;
    private BigDecimal maxCost;
    private Instant fromDate;
    private Instant toDate;
    private BookingStatus bookingStatus;
}
