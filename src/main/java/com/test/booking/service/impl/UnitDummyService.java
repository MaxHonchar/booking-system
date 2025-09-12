package com.test.booking.service.impl;

import com.test.booking.domain.Unit;
import com.test.booking.domain.UnitProperties;
import com.test.booking.enums.AccommodationType;
import com.test.booking.repository.UnitRepository;
import com.test.booking.service.IEventService;
import com.test.booking.service.IUnitDummyService;
import lombok.RequiredArgsConstructor;
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

    private final UnitRepository unitRepository;
    private final IEventService eventService;
    private final Random random = new Random();

    @Transactional
    @Override
    public void initUnits() {
        AccommodationType[] accommodationTypes = AccommodationType.values();

        for (int i = 0; i < AMOUNT_OF_UNITS; i++) {
            Long eventId = ONE + random.nextLong(3);
            BigDecimal cost = BigDecimal.valueOf(20 + random.nextDouble(THOUSAND_NUMBER));
            int rooms = ONE.intValue() + random.nextInt(10);
            int floor = ONE.intValue() + random.nextInt(90);
            int accommodationTypeIndex = ONE.intValue() + random.nextInt(2);
            int index = i + random.nextInt(THOUSAND_NUMBER);
            String description = "Unit #" + index;

            Unit unit = new Unit();
            unit.setCost(cost);
            unit.setDescription(description);

            eventService.findById(eventId)
                    .ifPresent(event -> {
                        event.getUnits().add(unit);
                        unit.getEvents().add(event);
                    });

            UnitProperties properties = new UnitProperties();
            properties.setRooms(rooms);
            properties.setFloor(floor);
            properties.setType(accommodationTypes[accommodationTypeIndex]);
            properties.setUnit(unit);

            unit.setProperties(Set.of(properties));

            unitRepository.save(unit);
        }
    }

}
