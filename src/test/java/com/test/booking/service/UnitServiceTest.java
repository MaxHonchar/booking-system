package com.test.booking.service.impl;

import com.test.booking.domain.Event;
import com.test.booking.domain.Unit;
import com.test.booking.domain.UnitProperties;
import com.test.booking.dtos.UnitDto;
import com.test.booking.dtos.UnitPropertiesDto;
import com.test.booking.dtos.UnitSearchDto;
import com.test.booking.enums.EventType;
import com.test.booking.mappers.UnitMapper;
import com.test.booking.mappers.UnitPropertiesMapper;
import com.test.booking.repository.IUnitCriteriaRepository;
import com.test.booking.repository.IUnitRepository;
import com.test.booking.service.IEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UnitServiceTest {

    @Mock
    private IUnitRepository unitRepository;
    @Mock
    private IUnitCriteriaRepository unitCriteriaRepository;
    @Mock
    private IEventService eventService;
    @Mock
    private UnitPropertiesMapper unitPropertiesMapper;
    @Mock
    private UnitMapper unitMapper;

    @InjectMocks
    private UnitService unitService;

    private Unit unit;
    private UnitDto unitDto;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        unit = new Unit();
        unit.setId(1L);
        unit.setDescription("Nice flat");
        unit.setCost(BigDecimal.valueOf(100));

        unitDto = new UnitDto();
        unitDto.setId(1L);
        unitDto.setDescription("Nice flat");
        unitDto.setCost(100);
    }

    @Test
    public void shouldReturnMappedDtos() {
        UnitSearchDto searchDto = UnitSearchDto.builder().build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Unit> page = new PageImpl<>(List.of(unit), pageable, 1);

        when(unitCriteriaRepository.findUnitsByFilters(searchDto, pageable)).thenReturn(page);
        when(unitMapper.map(List.of(unit))).thenReturn(List.of(unitDto));

        Page<UnitDto> result = unitService.getAvailableUnits(searchDto, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getDescription()).isEqualTo("Nice flat");
    }

    @Test
    public void shouldReturnEmptyWhenNoUnitsFound() {
        UnitSearchDto searchDto = UnitSearchDto.builder().build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Unit> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(unitCriteriaRepository.findUnitsByFilters(searchDto, pageable)).thenReturn(emptyPage);

        Page<UnitDto> result = unitService.getAvailableUnits(searchDto, pageable);

        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    public void shouldReturnUnitWhenExists() {
        when(unitRepository.findById(1L)).thenReturn(Optional.of(unit));

        Optional<Unit> result = unitService.getUnitById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getDescription()).isEqualTo("Nice flat");
    }

    @Test
    public void shouldReturnEmptyWhenNotExists() {
        when(unitRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Unit> result = unitService.getUnitById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldCreateUnitWithProperties() {
        unitDto.setProperties(Set.of(new UnitPropertiesDto()));

        Event event = new Event();
        event.setId(10L);
        event.setType(EventType.CREATE);

        UnitProperties prop = new UnitProperties();
        prop.setId(100L);

        when(eventService.findByType(EventType.CREATE)).thenReturn(Optional.of(event));
        when(unitRepository.save(any(Unit.class))).thenReturn(unit);
        when(unitPropertiesMapper.map(any(Set.class))).thenReturn(Set.of(prop));
        when(unitMapper.map(any(Unit.class))).thenReturn(unitDto);

        Optional<UnitDto> result = unitService.createUnit(unitDto);

        assertThat(result).isPresent();
        verify(unitRepository).save(any(Unit.class));
        verify(unitPropertiesMapper).map(any(Set.class));
        verify(unitMapper).map(any(Unit.class));
    }

    @Test
    public void shouldCreateUnitWithoutProperties() {
        unitDto.setProperties(Collections.emptySet());

        Event event = new Event();
        event.setId(10L);
        event.setType(EventType.CREATE);

        when(eventService.findByType(EventType.CREATE)).thenReturn(Optional.of(event));
        when(unitRepository.save(any(Unit.class))).thenReturn(unit);
        when(unitMapper.map(any(Unit.class))).thenReturn(unitDto);

        Optional<UnitDto> result = unitService.createUnit(unitDto);

        assertThat(result).isPresent();
        verify(unitRepository).save(any(Unit.class));
        verify(unitPropertiesMapper, never()).map(any(Set.class));
    }

    @Test
    public void shouldThrowExceptionWhenEventNotFound() {
        unitDto.setProperties(Collections.emptySet());
        when(eventService.findByType(EventType.CREATE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> unitService.createUnit(unitDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event not found");
    }
}
