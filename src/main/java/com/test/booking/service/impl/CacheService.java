package com.test.booking.service.impl;

import com.test.booking.dtos.UnitSearchDto;
import com.test.booking.enums.BookingStatus;
import com.test.booking.enums.EventType;
import com.test.booking.repository.IUnitCriteriaRepository;
import com.test.booking.service.ICacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService implements ICacheService {

    private static final String UNITS_TOTAL_KEY = "availableUnitsCount";
    private final IUnitCriteriaRepository unitCriteriaRepository;
    private final RedisTemplate<String, Long> redisTemplate;

    @Value("${spring.data.redis.expiration}")
    private Integer expirationMinutes;

    @Override
    public Long getTotalUnits() {
        log.info("getTotalUnits from cache");
        return Optional.ofNullable(redisTemplate.opsForValue().get(UNITS_TOTAL_KEY))
                .orElseGet(() -> {
                    log.info("not found in cache, trying to get total units from DB and set to cache");
                    updateTotalUnits();
                    return redisTemplate.opsForValue().get(UNITS_TOTAL_KEY);
                });
    }

    @Override
    public void updateTotalUnits() {
        UnitSearchDto searchDto = getUnitSearch();
        log.info("Get totalUnits: by booking status {} and event type {}", searchDto.getBookingStatus(), searchDto.getEventType());
        Long totalUnits = unitCriteriaRepository.getTotalUnits(searchDto);
        log.info("Get totalUnits: {}", totalUnits);
        redisTemplate.opsForValue().set(UNITS_TOTAL_KEY, totalUnits, expirationMinutes, TimeUnit.MINUTES);
    }

    private UnitSearchDto getUnitSearch() {
        return UnitSearchDto.builder()
                .eventType(EventType.DELETE)
                .bookingStatus(BookingStatus.BOOKED)
                .build();
    }
}
