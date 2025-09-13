package com.test.booking.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class CreateBookingDto {
    private Long unitId;
    private String userEmail;
    private LocalDate checkIn;
    private LocalDate checkOut;
}
