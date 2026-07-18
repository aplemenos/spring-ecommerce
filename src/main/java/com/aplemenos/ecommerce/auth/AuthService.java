package com.aplemenos.ecommerce.auth;

import com.aplemenos.ecommerce.auth.dto.AuthResponse;
import com.aplemenos.ecommerce.auth.dto.LoginRequest;
import com.aplemenos.ecommerce.auth.dto.RegisterRequest;
import com.aplemenos.ecommerce.common.exception.DuplicateResourceException;
import com.aplemenos.ecommerce.user.Role;
import com.aplemenos.ecommerce.user.User;
import com.aplemenos.ecommerce.user.UserRepository;
import com.aplemenos.ecommerce.user.mapper.UserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already registered: " + request.email());
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        User saved = userRepository.save(user);
        return buildResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        // Throws BadCredentialsException on a wrong email/password -> handled as 401
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("No user with email " + request.email()));

        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        String token = jwtService.generateToken(user);
        return AuthResponse.bearer(token, jwtService.getExpirationSeconds(), userMapper.toDto(user));
    }
}
