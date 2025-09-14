package com.test.booking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnitDto {
    private Long id;
    private double cost;
    private String description;
    private Set<UnitPropertiesDto> properties;
}
