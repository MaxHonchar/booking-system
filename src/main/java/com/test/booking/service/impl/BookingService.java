package com.test.booking.service.impl;

import com.test.booking.domain.Booking;
import com.test.booking.domain.Payment;
import com.test.booking.domain.Unit;
import com.test.booking.domain.User;
import com.test.booking.dtos.BookingDto;
import com.test.booking.dtos.CreateBookingDto;
import com.test.booking.enums.BookingStatus;
import com.test.booking.enums.PaymentStatus;
import com.test.booking.mappers.BookingMapper;
import com.test.booking.repository.IBookingRepository;
import com.test.booking.repository.IPaymentRepository;
import com.test.booking.service.IBookingService;
import com.test.booking.service.ICacheService;
import com.test.booking.service.IUnitService;
import com.test.booking.service.IUserService;
import com.test.booking.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static com.test.booking.utils.CommonUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final IUnitService unitService;
    private final IUserService userService;
    private final ICacheService cacheService;
    private final IBookingRepository bookingRepository;
    private final IPaymentRepository paymentRepository;
    private final BookingMapper bookingMapper;

    @Value("${booking.expiration}")
    private Integer expirationTime;

    @Transactional
    @Override
    public Optional<BookingDto> save(CreateBookingDto dto) {

        final Unit unit = unitService.getUnitById(dto.getUnitId())
                .orElseThrow(() -> new IllegalArgumentException("Unit not found"));
        final User user = userService.getOrSaveUser(dto.getUserEmail());

        final Instant checkIn = convertLocalDateToInstant(dto.getCheckIn())
                .orElseThrow(() -> new IllegalArgumentException("CheckIn not found"));

        final Instant checkOut = convertLocalDateToInstant(dto.getCheckOut())
                .orElseThrow(() -> new IllegalArgumentException("CheckOut not found"));

        log.info("creating booking for unit: {}, user: {}, checkIn: {}, checkOut: {}", unit.getId(), user.getEmail(), checkIn, checkOut);

        Booking booking = new Booking();
        booking.setUnit(unit);
        booking.setUser(user);
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setStatus(BookingStatus.CREATED);
        bookingRepository.save(booking);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(unit.getCost());
        payment.setStatus(PaymentStatus.NOT_PAID);
        payment.setExpiresAt(Instant.now().plus(expirationTime, ChronoUnit.MINUTES));
        paymentRepository.save(payment);

        return Optional.of(bookingMapper.map(booking));
    }

    @Transactional
    @Override
    public Optional<BookingDto> pay(Long bookingId, Double amount) {
        BigDecimal decimal = CommonUtils.getDecimal(amount);
        return bookingRepository.findById(bookingId)
                .filter(booking -> Objects.nonNull(booking.getPayment()))
                .filter(Predicate.not(booking -> booking.getStatus().equals(BookingStatus.BOOKED)))
                .filter(Predicate.not(booking -> booking.getPayment().getStatus().equals(PaymentStatus.PAID)))
                .filter(Predicate.not(booking -> booking.getPayment().getStatus().equals(PaymentStatus.CANCELED)))
                .filter(booking -> booking.getPayment().getAmount().equals(decimal))
                .map(booking -> updatePaymentBooking(booking, BookingStatus.BOOKED, PaymentStatus.PAID))
                .map(bookingMapper::map);
    }

    @Transactional
    @Override
    public Optional<BookingDto> cancel(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .filter(booking -> Objects.nonNull(booking.getPayment()))
                .filter(Predicate.not(booking -> booking.getPayment().getStatus().equals(PaymentStatus.CANCELED)))
                .map(booking -> updatePaymentBooking(booking, BookingStatus.CANCELED, PaymentStatus.CANCELED))
                .map(bookingMapper::map);
    }

    private Booking updatePaymentBooking(Booking booking, BookingStatus bookingStatus, PaymentStatus status) {
        Payment payment = booking.getPayment();
        payment.setStatus(status);
        if(PaymentStatus.PAID.equals(status)) {
            payment.setPaidAt(Instant.now());
        }

        booking.setPayment(payment);
        booking.setStatus(bookingStatus);
        cacheService.updateTotalUnits();
        return bookingRepository.save(booking);
    }

    @Scheduled(fixedRate = 60000)
    public void cleanupExpired() {
        bookingRepository.findBookingsByStatusAndPaymentExpiresAtAfter(BookingStatus.CREATED, Instant.now())
                .forEach(booking -> {
                    updatePaymentBooking(booking, BookingStatus.CANCELED, PaymentStatus.CANCELED);
                });
        cacheService.updateTotalUnits();
    }
}
