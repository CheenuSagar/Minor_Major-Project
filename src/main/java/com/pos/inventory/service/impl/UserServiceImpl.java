package com.pos.inventory.service.impl;

import com.pos.inventory.dto.auth.UserDto;
import com.pos.inventory.entity.Role;
import com.pos.inventory.entity.RoleType;
import com.pos.inventory.entity.User;
import com.pos.inventory.exception.ResourceNotFoundException;
import com.pos.inventory.repository.RoleRepository;
import com.pos.inventory.repository.UserRepository;
import com.pos.inventory.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserDto updateStatus(Long id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setEnabled(enabled);
        User updated = userRepository.save(user);
        log.info("Updated status of user id {} to enabled={}", id, enabled);
        return UserDto.fromEntity(updated);
    }

    @Override
    @Transactional
    public UserDto assignRoles(Long id, Set<RoleType> roleTypes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        Set<Role> roles = new HashSet<>();
        for (RoleType roleType : roleTypes) {
            Role role = roleRepository.findByName(roleType)
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .name(roleType)
                            .description("Role " + roleType.name())
                            .build()));
            roles.add(role);
        }

        user.setRoles(roles);
        User updated = userRepository.save(user);
        log.info("Assigned roles {} to user id {}", roleTypes, id);
        return UserDto.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserDto.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElse(null);
    }
}
