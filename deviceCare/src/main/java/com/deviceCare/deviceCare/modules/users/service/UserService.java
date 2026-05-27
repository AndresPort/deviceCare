package com.deviceCare.deviceCare.modules.users.service;

import com.deviceCare.deviceCare.common.exception.BusinessException;
import com.deviceCare.deviceCare.modules.users.dto.UserRequest;
import com.deviceCare.deviceCare.modules.users.dto.UserResponse;
import com.deviceCare.deviceCare.modules.users.model.Role;
import com.deviceCare.deviceCare.modules.users.model.User;
import com.deviceCare.deviceCare.modules.users.repository.RoleRepository;
import com.deviceCare.deviceCare.modules.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw BusinessException.conflict("El email ya está registrado");
        }

        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> BusinessException.notFound("Rol no encontrado: " + roleName)))
                .collect(Collectors.toSet());

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRoles(roles);

        return toResponse(userRepository.save(user));
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .filter(user -> user.getDeletedAt() == null)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse findById(UUID id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.notFound("Usuario no encontrado"));
        return toResponse(user);
    }

    @Transactional
    public UserResponse update(UUID id, UserRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.notFound("Usuario no encontrado"));

        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw BusinessException.conflict("El email ya está registrado");
        }

        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> BusinessException.notFound("Rol no encontrado: " + roleName)))
                .collect(Collectors.toSet());

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRoles(roles);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(UUID id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.notFound("Usuario no encontrado"));
        user.setDeletedAt(OffsetDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public UserResponse toggleActive(UUID id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.notFound("Usuario no encontrado"));
        user.setActive(!user.isActive());
        return toResponse(userRepository.save(user));
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));
        return response;
    }
}