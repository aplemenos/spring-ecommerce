package com.aplemenos.ecommerce.user;

import com.aplemenos.ecommerce.user.dto.UpdateRoleRequest;
import com.aplemenos.ecommerce.user.dto.UserDto;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** Any authenticated user can read their own profile. */
    @GetMapping("/me")
    public UserDto me(Principal principal) {
        return userService.findByEmail(principal.getName());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto getById(@PathVariable Long id) {
        return userService.findById(id);
    }

    /** Promote or demote a user. Admins only. */
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateRole(@PathVariable Long id,
                              @Valid @RequestBody UpdateRoleRequest request,
                              Principal principal) {
        return userService.updateRole(id, request.role(), principal.getName());
    }
}
