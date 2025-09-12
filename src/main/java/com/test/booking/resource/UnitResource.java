package com.test.booking.resource;

import com.test.booking.dtos.UnitDto;
import com.test.booking.dtos.UnitSearchDto;
import com.test.booking.service.IUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/unit")
@RequiredArgsConstructor
public class UnitResource {

    private final IUnitService unitService;

    @GetMapping
    public Page<UnitDto> findUnitsByFilters(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) Double minCost,
            @RequestParam(required = false) Double maxCost,
            Pageable pageable) {
        UnitSearchDto searchDto = getUnitSearchDto(fromDate, toDate, minCost, maxCost);
        return unitService.getAvailableUnits(searchDto, pageable);
    }

    private UnitSearchDto getUnitSearchDto(LocalDate fromDate,
                                           LocalDate toDate,
                                           Double minCost,
                                           Double maxCost) {
        return UnitSearchDto.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .minCost(minCost)
                .maxCost(maxCost)
                .build();

    }

}
