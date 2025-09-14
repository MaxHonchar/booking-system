package com.test.booking.service;

import com.test.booking.dtos.BookingDto;
import com.test.booking.dtos.CreateBookingDto;
import com.test.booking.enums.BookingStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static com.test.booking.utils.BookingUtilsTest.*;
import static com.test.booking.utils.CommonUtils.convertLocalDateToInstant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class BookingServiceIntegrationTest {

    @Autowired
    private IBookingService bookingService;

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres");

    @Value("${local.server.port}")
    private int port;

    @Test
    public void shouldSave() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(3);
        CreateBookingDto dto = getCreateBookingDto(checkIn, checkOut);
        Optional<BookingDto> bookingDto = bookingService.create(dto);
        assertTrue(bookingDto.isPresent());
        assertEquals(bookingDto.get().getStatus(), BookingStatus.CREATED.name());
        assertEquals(USER_EMAIL, bookingDto.get().getUser().getEmail());
        assertEquals(convertLocalDateToInstant(checkIn).get(), bookingDto.get().getCheckIn());
        assertEquals(convertLocalDateToInstant(checkOut).get(), bookingDto.get().getCheckOut());
        assertEquals(BOOKING_ID, bookingDto.get().getId());
        assertTrue(Objects.nonNull(bookingDto.get().getUnit()));
        assertEquals(UNIT_ID, bookingDto.get().getUnit().getId());
        assertEquals(UNIT_COST, bookingDto.get().getUnit().getCost());
        assertTrue(CollectionUtils.isNotEmpty(bookingDto.get().getUnit().getProperties()));
    }

    @Test
    public void shouldReturnBookingWhenSuccessfullyPay() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(3);
        CreateBookingDto dto = getCreateBookingDto(checkIn, checkOut);
        dto.setUnitId(2L);
        Optional<BookingDto> bookingDto = bookingService.create(dto);
        assertTrue(bookingDto.isPresent());

        Optional<BookingDto> bookingDto1 = bookingService.pay(bookingDto.get().getId(), UNIT_2_COST);
        assertTrue(bookingDto1.isPresent());
        assertEquals(bookingDto.get().getId(), bookingDto1.get().getId());
        assertEquals(bookingDto1.get().getStatus(), BookingStatus.BOOKED.name());
    }

    @Test
    public void shouldBookingCanceled() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(3);
        CreateBookingDto dto = getCreateBookingDto(checkIn, checkOut);
        dto.setUnitId(3L);
        Optional<BookingDto> bookingDto = bookingService.create(dto);
        assertTrue(bookingDto.isPresent());

        Optional<BookingDto> bookingDto1 = bookingService.cancel(bookingDto.get().getId());
        assertTrue(bookingDto1.isPresent());
        assertEquals(bookingDto.get().getId(), bookingDto1.get().getId());
        assertEquals(bookingDto1.get().getStatus(), BookingStatus.CANCELED.name());
    }


}
