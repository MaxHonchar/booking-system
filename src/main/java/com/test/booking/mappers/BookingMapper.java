package com.test.booking.mappers;

import com.test.booking.domain.Booking;
import com.test.booking.dtos.BookingDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDto map(Booking booking);
}
