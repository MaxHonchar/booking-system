package com.test.booking.utils;

import com.test.booking.dtos.BookingDto;
import com.test.booking.dtos.CreateBookingDto;
import com.test.booking.dtos.UserDto;

import java.time.LocalDate;

import static com.test.booking.utils.CommonUtils.convertLocalDateToInstant;

public class BookingUtilsTest {

    public static final Long BOOKING_ID = 1L;
    public static final Long UNIT_ID = 1L;
    public static final Long USER_ID = 1L;
    public static final String USER_EMAIL = "test@test.com";
    public static final double UNIT_COST = 50.5;
    public static final double UNIT_2_COST = 45.5;

    public static CreateBookingDto getCreateBookingDto(LocalDate now, LocalDate checkOut) {
        return CreateBookingDto.builder()
                .unitId(UNIT_ID)
                .userEmail(USER_EMAIL)
                .checkIn(now)
                .checkOut(checkOut)
                .build();
    }

    public static BookingDto getBookingDto(CreateBookingDto dto) {
        UserDto userDto = UserDto.builder()
                .id(USER_ID)
                .email(dto.getUserEmail())
                .build();

        return BookingDto.builder()
                .id(BOOKING_ID)
                .checkIn(convertLocalDateToInstant(dto.getCheckIn()).get())
                .checkOut(convertLocalDateToInstant(dto.getCheckOut()).get())
                .user(userDto)
                .build();
    }

}
