package com.pos.inventory.controller;

import com.pos.inventory.dto.auth.AuthResponse;
import com.pos.inventory.dto.auth.LoginRequest;
import com.pos.inventory.dto.auth.RegisterRequest;
import com.pos.inventory.dto.auth.UserDto;
import com.pos.inventory.dto.common.ApiResponse;
import com.pos.inventory.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication & Access", description = "Endpoints for user registration, JWT login, and logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new customer account", description = "Creates a new user with CUSTOMER role and encrypted password")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterRequest request) {
        UserDto userDto = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User registered successfully", userDto));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and generate JWT token", description = "Verifies username/password and issues HS256 JWT valid for 120 minutes")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Invalidate and blacklist current JWT token", description = "Blacklists the active bearer token in the database to prevent reuse")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        authService.logout(authHeader);
        return ResponseEntity.ok(ApiResponse.message("Logged out successfully. Token invalidated."));
    }
}
