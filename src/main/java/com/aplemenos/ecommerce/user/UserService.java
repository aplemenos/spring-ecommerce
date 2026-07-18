package com.aplemenos.ecommerce.user;

import com.aplemenos.ecommerce.common.exception.ResourceNotFoundException;
import com.aplemenos.ecommerce.user.dto.UserDto;
import com.aplemenos.ecommerce.user.mapper.UserMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user no longer exists: " + email));
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto findById(Long id) {
        return userMapper.toDto(getUserOrThrow(id));
    }

    /**
     * Changes a user's role. Admin-only, enforced at the controller.
     *
     * <p>Refusing self-demotion preserves the invariant "at least one admin exists":
     * the caller is always an admin, and cannot demote themselves, so every
     * successful call leaves at least the caller with the ADMIN role. Demoting a
     * different admin is therefore always safe.
     *
     * <p>The invariant only covers this endpoint — a direct database edit, or a
     * future user-deletion endpoint, could still remove the last admin.
     */
    @Transactional
    public UserDto updateRole(Long id, Role newRole, String actingUserEmail) {
        User user = getUserOrThrow(id);

        if (user.getEmail().equals(actingUserEmail) && newRole != Role.ADMIN) {
            throw new IllegalStateException("Admins cannot demote themselves");
        }

        user.setRole(newRole);
        return userMapper.toDto(userRepository.save(user));
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("User", id));
    }
}
