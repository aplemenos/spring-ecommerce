package com.aplemenos.ecommerce.user;

import com.aplemenos.ecommerce.common.exception.ResourceNotFoundException;
import com.aplemenos.ecommerce.user.dto.UserDto;
import com.aplemenos.ecommerce.user.mapper.UserMapper;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /** Returns the currently authenticated user, resolved from the JWT subject. */
    @GetMapping("/me")
    public UserDto me(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user no longer exists"));
    }
}
