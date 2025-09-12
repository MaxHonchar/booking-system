package com.test.booking.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookingDto {
    private Long id;
    private UnitDto unit;
    private UserDto user;
    private LocalDate checkIn;
    private LocalDate checkOut;
}
