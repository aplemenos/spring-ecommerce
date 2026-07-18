package com.aplemenos.ecommerce.user.mapper;

import com.aplemenos.ecommerce.user.User;
import com.aplemenos.ecommerce.user.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /** Password is simply absent from UserDto, so it can never be mapped out. */
    UserDto toDto(User user);
}
