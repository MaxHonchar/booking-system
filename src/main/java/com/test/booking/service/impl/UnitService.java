package com.test.booking.service.impl;

import com.test.booking.domain.Event;
import com.test.booking.domain.Unit;
import com.test.booking.domain.UnitProperties;
import com.test.booking.dtos.UnitDto;
import com.test.booking.dtos.UnitSearchDto;
import com.test.booking.enums.EventType;
import com.test.booking.mappers.UnitMapper;
import com.test.booking.mappers.UnitPropertiesMapper;
import com.test.booking.repository.IUnitCriteriaRepository;
import com.test.booking.repository.IUnitRepository;
import com.test.booking.service.ICacheService;
import com.test.booking.service.IEventService;
import com.test.booking.service.IUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.test.booking.utils.CommonUtils.getCostWithBookingPercentage;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnitService implements IUnitService {

    private final ICacheService cacheService;
    private final IUnitRepository unitRepository;
    private final IUnitCriteriaRepository unitCriteriaRepository;
    private final IEventService eventService;
    private final UnitPropertiesMapper unitPropertiesMapper;
    private final UnitMapper unitMapper;

    @Override
    public Page<UnitDto> getAvailableUnits(UnitSearchDto searchDto, Pageable pageable) {
        log.info("getAvailableUnits by type{} minCost: {}, maxCost : {}, fromDate: {}, toDate {}",
                searchDto.getAccommodationType(),
                searchDto.getMinCost(), searchDto.getMaxCost(), searchDto.getFromDate(), searchDto.getToDate());
        Page<Unit> unitPage = unitCriteriaRepository.findUnitsByFilters(searchDto, pageable);
        log.info("find total units count: {} and pages {}", unitPage.getTotalElements(), unitPage.getTotalPages());
        Page<UnitDto> unitDtoPage = convert(unitPage);
        log.info("converted unit count: {} and total {}", unitDtoPage.getSize(), unitDtoPage.getTotalElements());
        return unitDtoPage;
    }

    @Override
    public Optional<Unit> getUnitById(Long id) {
        return unitRepository.findById(id);
    }

    @Transactional
    @Override
    public Optional<UnitDto> createUnit(UnitDto dto) {
        Event event = eventService.findByType(EventType.CREATE)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        Unit unit = new Unit();
        unit.setCost(getCostWithBookingPercentage(dto.getCost()));
        unit.getEvents().add(event);
        unit.setDescription(dto.getDescription());

        unitRepository.save(unit);

        if(CollectionUtils.isNotEmpty(dto.getProperties())) {
            Set<UnitProperties> properties = unitPropertiesMapper.map(dto.getProperties());
            properties.forEach(prop -> prop.setUnit(unit));
            unit.getProperties().addAll(properties);
        }

        return Optional.of(unitMapper.map(unit));
    }

    private Page<UnitDto> convert(Page<Unit> unitPage) {
        List<Unit> units = unitPage.getContent();
        if(CollectionUtils.isNotEmpty(units)) {
            List<UnitDto> dtos = unitMapper.map(units);
            return new PageImpl<>(dtos, unitPage.getPageable(), unitPage.getTotalElements());
        }
        return new PageImpl<>(new ArrayList<>());
    }
}
