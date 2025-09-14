package com.test.booking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingDto {
    private Long unitId;
    private String userEmail;
    private LocalDate checkIn;
    private LocalDate checkOut;
}
