package com.aplemenos.ecommerce.auth.dto;

import com.aplemenos.ecommerce.user.dto.UserDto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresInSeconds,
        UserDto user
) {

    public static AuthResponse bearer(String token, long expiresInSeconds, UserDto user) {
        return new AuthResponse(token, "Bearer", expiresInSeconds, user);
    }
}
