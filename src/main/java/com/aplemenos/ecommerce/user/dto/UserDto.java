package com.aplemenos.ecommerce.user.dto;

import com.aplemenos.ecommerce.user.Role;
import java.time.Instant;

/** Public view of a user — deliberately has no password field. */
public record UserDto(
        Long id,
        String name,
        String email,
        Role role,
        Instant createdAt
) {
}
