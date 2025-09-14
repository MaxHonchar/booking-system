package com.test.booking.service;

import com.test.booking.domain.Unit;
import com.test.booking.domain.UnitProperties;
import com.test.booking.dtos.UnitDto;
import com.test.booking.dtos.UnitPropertiesDto;
import com.test.booking.dtos.UnitSearchDto;
import com.test.booking.enums.AccommodationType;
import com.test.booking.enums.EventType;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static com.test.booking.utils.BookingUtilsTest.*;
import static com.test.booking.utils.CommonUtils.getDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UnitServiceIntegrationTest {

    @Autowired
    private IUnitService unitService;

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres");

    @Value("${local.server.port}")
    private int port;

    @Test
    public void shouldReturnUnit() {
        BigDecimal decimal = getDecimal(UNIT_COST);
        Optional<Unit> unit = unitService.getUnitById(UNIT_ID);
        assertTrue(unit.isPresent());
        assertEquals(UNIT_ID, unit.get().getId());
        assertEquals(decimal, unit.get().getCost());
    }

    @Test
    public void shouldCreateUnit() {
        double cost = 77.7;
        int floor = 77;
        int rooms = 7;
        String description = "Test description";
        UnitPropertiesDto unitProperties = UnitPropertiesDto.builder()
                .type(AccommodationType.HOME.name())
                .floor(floor)
                .rooms(rooms)
                .build();

        UnitDto unitDto = UnitDto.builder()
                .cost(cost)
                .description(description)
                .properties(Set.of(unitProperties))
                .build();

        Optional<UnitDto> optional = unitService.createUnit(unitDto);
        assertTrue(optional.isPresent());
        assertEquals(description, optional.get().getDescription());
        assertTrue(CollectionUtils.isNotEmpty(optional.get().getProperties()));

        UnitPropertiesDto propertiesDto = optional.get().getProperties().iterator().next();
        assertEquals(floor, propertiesDto.getFloor());
        assertEquals(rooms, propertiesDto.getRooms());
        assertEquals(AccommodationType.HOME.name(), propertiesDto.getType());
    }

    @Test
    public void shouldReturnsUnitsWhenSearch() {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(0,pageSize);
        UnitSearchDto searchDto = UnitSearchDto.builder()
                .accommodationType(AccommodationType.APARTMENTS)
                .eventType(EventType.DELETE)
                .build();

        Page<UnitDto> unitDtos = unitService.getAvailableUnits(searchDto, pageable);
        assertTrue(CollectionUtils.isNotEmpty(unitDtos.getContent()));
        assertTrue(unitDtos.hasContent());
        assertEquals(pageSize, unitDtos.getSize());
        assertTrue(unitDtos.getTotalElements() > pageSize);

        UnitDto unitDto = unitDtos.getContent().iterator().next();
        assertEquals(AccommodationType.APARTMENTS.name(), unitDto.getProperties().iterator().next().getType());
    }


}
