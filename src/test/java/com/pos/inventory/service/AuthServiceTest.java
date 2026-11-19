package com.pos.inventory.service;

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
import com.pos.inventory.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private User sampleUser;
    private Role customerRole;

    @BeforeEach
    void setUp() {
        customerRole = Role.builder()
                .id(2L)
                .name(RoleType.ROLE_CUSTOMER)
                .description("Customer")
                .build();

        sampleUser = User.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .username("johndoe")
                .password("encoded_pass")
                .enabled(true)
                .roles(Set.of(customerRole))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void register_Success() {
        RegisterRequest request = RegisterRequest.builder()
                .name("John Doe")
                .email("john@example.com")
                .username("johndoe")
                .password("Secret@123")
                .build();

        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleType.ROLE_CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("Secret@123")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserDto result = authService.register(request);

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertTrue(result.getRoles().contains(RoleType.ROLE_CUSTOMER.name()));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException if username is already taken")
    void register_DuplicateUsername_ThrowsException() {
        RegisterRequest request = RegisterRequest.builder()
                .name("John Doe")
                .email("john@example.com")
                .username("johndoe")
                .password("Secret@123")
                .build();

        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully login and return JWT token")
    void login_Success() {
        LoginRequest request = LoginRequest.builder()
                .username("johndoe")
                .password("Secret@123")
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(sampleUser);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt.token.here");
        when(tokenProvider.getExpirationMs()).thenReturn(7200000L);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt.token.here", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("johndoe", response.getUsername());
        assertEquals(7200000L, response.getExpiresInMs());
    }

    @Test
    @DisplayName("Should successfully blacklist token on logout")
    void logout_Success() {
        String bearerToken = "Bearer valid.token.string";
        Date mockDate = new Date();

        when(tokenProvider.getExpirationDateFromJwt("valid.token.string")).thenReturn(mockDate);

        authService.logout(bearerToken);

        verify(tokenBlacklistRepository, times(1)).save(any(TokenBlacklist.class));
    }
}
