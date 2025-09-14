package com.test.booking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private UnitDto unit;
    private UserDto user;
    private Instant checkIn;
    private Instant checkOut;
    private String status;
}
