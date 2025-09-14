package com.test.booking.repository;

import com.test.booking.domain.Booking;
import com.test.booking.enums.BookingStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface IBookingRepository extends CrudRepository<Booking, Long> {

    List<Booking> findBookingsByStatusAndPaymentExpiresAtAfter(BookingStatus status, Instant expiresAt);

}
