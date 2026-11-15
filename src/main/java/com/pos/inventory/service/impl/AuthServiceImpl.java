package com.pos.inventory.service.impl;

import com.pos.inventory.dto.auth.AuthResponse;
import com.pos.inventory.dto.auth.LoginRequest;
import com.pos.inventory.dto.auth.RegisterRequest;
import com.pos.inventory.dto.auth.UserDto;
import com.pos.inventory.entity.Role;
import com.pos.inventory.entity.RoleType;
import com.pos.inventory.entity.TokenBlacklist;
import com.pos.inventory.entity.User;
import com.pos.inventory.exception.BadRequestException;
import com.pos.inventory.repository.RoleRepository;
import com.pos.inventory.repository.TokenBlacklistRepository;
import com.pos.inventory.repository.UserRepository;
import com.pos.inventory.security.CustomUserDetails;
import com.pos.inventory.security.JwtTokenProvider;
import com.pos.inventory.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username '" + request.getUsername() + "' is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email '" + request.getEmail() + "' is already registered");
        }

        Role customerRole = roleRepository.findByName(RoleType.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.ROLE_CUSTOMER)
                        .description("Default customer role")
                        .build()));

        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);

        User user = User.builder()
                .fullName(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .username(request.getUsername().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Registered new user with id: {}, username: {}", savedUser.getId(), savedUser.getUsername());
        return UserDto.fromEntity(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername().trim().toLowerCase(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(r -> r.getAuthority())
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .expiresInMs(tokenProvider.getExpirationMs())
                .userId(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .fullName(userDetails.getFullName())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public void logout(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            Date expiryDate = tokenProvider.getExpirationDateFromJwt(token);
            LocalDateTime expiry = LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault());

            TokenBlacklist blacklist = TokenBlacklist.builder()
                    .token(token)
                    .expiryDate(expiry)
                    .revokedAt(LocalDateTime.now())
                    .build();

            tokenBlacklistRepository.save(blacklist);
            log.info("Token successfully invalidated and blacklisted");
        }
    }
}
