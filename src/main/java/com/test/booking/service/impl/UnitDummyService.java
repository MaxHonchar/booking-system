package com.test.booking.service.impl;

import com.test.booking.domain.Unit;
import com.test.booking.domain.UnitProperties;
import com.test.booking.enums.AccommodationType;
import com.test.booking.repository.IUnitRepository;
import com.test.booking.service.IEventService;
import com.test.booking.service.IUnitDummyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UnitDummyService implements IUnitDummyService {

    private static final Long ONE = 1L;
    private static final int AMOUNT_OF_UNITS = 90;
    private static final int THOUSAND_NUMBER = 1000;
    private static final int ONE_HUNDRED = 100;
    private static final int MIN_COST = 20;
    private static final int MAX_EVENT_ID = 3;
    private static final int MAX_ROOM = 10;
    private static final int MAX_FLOOR = 90;
    private static final int ACCOMODATION_BOUND = 2;
    private static final double COEFFICIENT = 15.0;

    private final IUnitRepository unitRepository;
    private final IEventService eventService;
    private final Random random = new Random();

    @Async
    @Transactional
    @Override
    public void initUnits() {
        AccommodationType[] accommodationTypes = AccommodationType.values();

        for (int i = 0; i < AMOUNT_OF_UNITS; i++) {

            Unit unit = new Unit();
            unit.setCost(getCost());
            unit.setDescription(getDescription(i));

            populateEvent(unit);

            Set<UnitProperties> properties = getUnitProperties(accommodationTypes, unit);
            unit.setProperties(properties);

            unitRepository.save(unit);
        }
    }

    private String getDescription(int i) {
        int index = i + random.nextInt(THOUSAND_NUMBER);
        return  "Unit #" + index;
    }

    private void populateEvent(Unit unit) {
        Long eventId = ONE + random.nextLong(MAX_EVENT_ID);
        eventService.findById(eventId)
                .ifPresent(event -> {
                    event.getUnits().add(unit);
                    unit.getEvents().add(event);
                });
    }

    private Set<UnitProperties> getUnitProperties(AccommodationType[] accommodationTypes, Unit unit) {
        int rooms = ONE.intValue() + random.nextInt(MAX_ROOM);
        int floor = ONE.intValue() + random.nextInt(MAX_FLOOR);
        int accommodationTypeIndex = random.nextInt(ACCOMODATION_BOUND);
        AccommodationType accommodationType = accommodationTypes[accommodationTypeIndex];
        UnitProperties properties = new UnitProperties();
        properties.setRooms(rooms);
        properties.setFloor(floor);
        properties.setType(accommodationType);
        properties.setUnit(unit);
        return Set.of(properties);
    }

    private BigDecimal getCost() {
        double cost = MIN_COST + random.nextDouble(THOUSAND_NUMBER);
        double percentage = COEFFICIENT / ONE_HUNDRED;
        return BigDecimal.valueOf(cost + cost * percentage);
    }

}
