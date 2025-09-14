package com.test.booking.mappers;

import com.test.booking.domain.UnitProperties;
import com.test.booking.dtos.UnitPropertiesDto;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface UnitPropertiesMapper {
    UnitPropertiesDto map(UnitProperties properties);
    Set<UnitProperties> map(Set<UnitPropertiesDto> dtos);
}
