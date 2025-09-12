package com.test.booking.mappers;

import com.test.booking.domain.User;
import com.test.booking.dtos.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto map(User unit);
}
