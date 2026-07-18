package com.aplemenos.ecommerce.user.dto;

import com.aplemenos.ecommerce.user.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(
        @NotNull Role role
) {
}
