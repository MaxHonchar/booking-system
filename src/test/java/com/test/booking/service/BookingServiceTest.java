package com.test.booking.service.impl;

import com.test.booking.domain.Booking;
import com.test.booking.domain.Payment;
import com.test.booking.domain.Unit;
import com.test.booking.domain.User;
import com.test.booking.dtos.BookingDto;
import com.test.booking.dtos.CreateBookingDto;
import com.test.booking.dtos.UserDto;
import com.test.booking.enums.BookingStatus;
import com.test.booking.enums.PaymentStatus;
import com.test.booking.mappers.BookingMapper;
import com.test.booking.repository.IBookingRepository;
import com.test.booking.repository.IPaymentRepository;
import com.test.booking.service.ICacheService;
import com.test.booking.service.IUnitService;
import com.test.booking.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static com.test.booking.utils.BookingUtilsTest.*;

public class BookingServiceTest {

    @Mock
    private IUnitService unitService;
    @Mock
    private IUserService userService;
    @Mock
    private ICacheService cacheService;
    @Mock
    private IBookingRepository bookingRepository;
    @Mock
    private IPaymentRepository paymentRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    private Unit unit;
    private User user;
    private Booking booking;
    private Payment payment;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(bookingService, "expirationTime", 15);

        unit = new Unit();
        unit.setId(1L);
        unit.setCost(BigDecimal.valueOf(100.0));

        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        booking = new Booking();
        booking.setId(1L);
        booking.setUnit(unit);
        booking.setUser(user);
        booking.setStatus(BookingStatus.CREATED);

        payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(unit.getCost());
        payment.setStatus(PaymentStatus.NOT_PAID);
        payment.setExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES));
        booking.setPayment(payment);
    }

    @Test
    void shouldCreateBookingAndPayment() {
        LocalDate now = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(3);
        CreateBookingDto dto = getCreateBookingDto(now, checkOut);
        BookingDto bookingDto = getBookingDto(dto);

        when(unitService.getUnitById(1L)).thenReturn(Optional.of(unit));
        when(userService.getOrSaveUser("test@test.com")).thenReturn(user);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(bookingMapper.map(any(Booking.class))).thenReturn(bookingDto);

        Optional<BookingDto> result = bookingService.create(dto);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getUser().getEmail()).isEqualTo("test@test.com");

        verify(bookingRepository).save(any(Booking.class));
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void shouldUpdateBookingAndPaymentWhenPay() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.map(any(Booking.class))).thenReturn(new BookingDto());

        Optional<BookingDto> result = bookingService.pay(1L, 100.0);

        assertThat(result).isPresent();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.BOOKED);
        assertThat(booking.getPayment().getStatus()).isEqualTo(PaymentStatus.PAID);
    }

    @Test
    void shouldReturnEmptyWhenAmountDoesNotMatch() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Optional<BookingDto> result = bookingService.pay(1L, 200.0);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateBookingWhenNotCanceled() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.map(any(Booking.class))).thenReturn(new BookingDto());

        Optional<BookingDto> result = bookingService.cancel(1L);

        assertThat(result).isPresent();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELED);
        assertThat(booking.getPayment().getStatus()).isEqualTo(PaymentStatus.CANCELED);
    }

    @Test
    void shouldReturnEmptyWhenAlreadyCanceled() {
        booking.getPayment().setStatus(PaymentStatus.CANCELED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Optional<BookingDto> result = bookingService.cancel(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCancelBookingsWhenExpired() {
        booking.setStatus(BookingStatus.CREATED);
        booking.getPayment().setExpiresAt(Instant.now().minusSeconds(60));

        when(bookingRepository.findBookingsByStatusAndPaymentExpiresAtAfter(eq(BookingStatus.CREATED), any()))
                .thenReturn(List.of(booking));

        bookingService.cleanupExpired();

        verify(bookingRepository).save(any(Booking.class));
        verify(cacheService).updateTotalUnits();
    }

}
