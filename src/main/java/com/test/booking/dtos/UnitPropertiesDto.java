package com.test.booking.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnitPropertiesDto {
    private Long id;
    private String type;
    private int rooms;
    private int floor;
}
