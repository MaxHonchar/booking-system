package com.test.booking.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class BookingDto {
    private Long id;
    private UnitDto unit;
    private UserDto user;
    private Instant checkIn;
    private Instant checkOut;
}
