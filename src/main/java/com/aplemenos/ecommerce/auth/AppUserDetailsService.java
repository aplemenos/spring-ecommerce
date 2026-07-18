package com.aplemenos.ecommerce.auth;

import com.aplemenos.ecommerce.user.User;
import com.aplemenos.ecommerce.user.UserRepository;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Bridges our {@link User} entity to Spring Security's UserDetails contract.
 * The username is the email address.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user with email " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                // Spring Security expects the ROLE_ prefix for hasRole(...) checks in M3
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .build();
    }
}
