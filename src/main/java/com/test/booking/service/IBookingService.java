package com.test.booking.service;

import com.test.booking.dtos.BookingDto;
import com.test.booking.dtos.CreateBookingDto;

import java.util.Optional;

public interface IBookingService {
    Optional<BookingDto> save(CreateBookingDto dto);
    Optional<BookingDto> pay(Long bookingId, Double amount);
    Optional<BookingDto> cancel(Long bookingId);
}
