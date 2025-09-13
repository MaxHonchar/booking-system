package com.test.booking.repository;

import com.test.booking.domain.Booking;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBookingRepository extends CrudRepository<Booking, Long> {
}
