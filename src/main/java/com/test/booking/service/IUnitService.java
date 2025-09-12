package com.test.booking.service;

import com.test.booking.dtos.UnitDto;
import com.test.booking.dtos.UnitSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface IUnitService {
    Page<UnitDto> getAvailableUnits(UnitSearchDto searchDto, Pageable pageable);
}
