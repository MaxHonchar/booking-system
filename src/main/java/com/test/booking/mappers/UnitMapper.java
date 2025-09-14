package com.test.booking.mappers;

import com.test.booking.domain.Unit;
import com.test.booking.dtos.UnitDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        UnitPropertiesMapper.class
})
public interface UnitMapper {
    UnitDto map(Unit unit);
    List<UnitDto> map(List<Unit> units);
}
