package com.test.booking.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class UnitDto {
    private Long id;
    private double cost;
    private String description;
    private Set<UnitPropertiesDto> properties;
}
