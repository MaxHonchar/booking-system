package com.test.booking.repository;

import com.test.booking.domain.Unit;
import com.test.booking.dtos.UnitSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUnitCriteriaRepository {

    Page<Unit> findUnitsByFilters(UnitSearchDto unitSearchDto, Pageable pageable);
    Long getTotalUnits(UnitSearchDto unitSearchDto);

}
