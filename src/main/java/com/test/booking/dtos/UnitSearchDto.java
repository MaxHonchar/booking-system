package com.test.booking.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UnitSearchDto {
    private Double minCost;
    private Double maxCost;
    private LocalDate fromDate;
    private LocalDate toDate;
}
