package com.aplemenos.ecommerce.user.mapper;

import com.aplemenos.ecommerce.user.User;
import com.aplemenos.ecommerce.user.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    /** Password is simply absent from UserDto, so it can never be mapped out. */
    UserDto toDto(User user);
}
