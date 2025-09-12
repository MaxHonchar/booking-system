package com.test.booking.service.impl;

import com.test.booking.domain.Unit;
import com.test.booking.dtos.UnitDto;
import com.test.booking.dtos.UnitSearchDto;
import com.test.booking.mappers.UnitMapper;
import com.test.booking.repository.UnitRepository;
import com.test.booking.service.IUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnitService implements IUnitService {

    private final UnitRepository unitRepository;
    private final UnitMapper unitMapper;

    @Override
    public Page<UnitDto> getAvailableUnits(UnitSearchDto searchDto, Pageable pageable) {
        log.info("getAvailableUnits by minCost: {}, maxCost : {}, fromDate: {}, toDate {}",
                searchDto.getMinCost(), searchDto.getMaxCost(), searchDto.getFromDate(), searchDto.getToDate());
        Page<Unit> unitPage = unitRepository.findUnitsByFilters(searchDto.getMinCost(), searchDto.getMaxCost(),
                searchDto.getFromDate(), searchDto.getToDate(), pageable);
        log.info("find units count: {}", unitPage.getTotalElements());
        Page<UnitDto> unitDtoPage = convert(unitPage);
        log.info("converted unit count: {}", unitDtoPage.getTotalElements());
        return unitDtoPage;
    }

    private Page<UnitDto> convert(Page<Unit> unitPage) {
        List<Unit> units = unitPage.getContent();
        if(CollectionUtils.isNotEmpty(units)) {
            List<UnitDto> dtos = unitMapper.map(units);
            return new PageImpl<>(dtos, unitPage.getPageable(), unitPage.getTotalElements());
        }
        return new PageImpl<>(new ArrayList<>(), unitPage.getPageable(), unitPage.getTotalElements());
    }
}
