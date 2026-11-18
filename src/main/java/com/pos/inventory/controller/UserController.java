package com.pos.inventory.controller;

import com.pos.inventory.dto.auth.AssignRoleRequest;
import com.pos.inventory.dto.auth.UserDto;
import com.pos.inventory.dto.auth.UserStatusUpdateRequest;
import com.pos.inventory.dto.common.ApiResponse;
import com.pos.inventory.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Store Manager endpoints for user activation and role administration")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('STORE_MANAGER')")
    @Operation(summary = "Enable or disable a user account", description = "Allowed only for STORE_MANAGER")
    public ResponseEntity<ApiResponse<UserDto>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        UserDto updated = userService.updateStatus(id, request.getEnabled());
        return ResponseEntity.ok(ApiResponse.ok("User status updated successfully", updated));
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('STORE_MANAGER')")
    @Operation(summary = "Assign roles to a user", description = "Allowed only for STORE_MANAGER")
    public ResponseEntity<ApiResponse<UserDto>> assignRoles(
            @PathVariable Long id,
            @Valid @RequestBody AssignRoleRequest request) {
        UserDto updated = userService.assignRoles(id, request.getRoles());
        return ResponseEntity.ok(ApiResponse.ok("User roles updated successfully", updated));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STORE_MANAGER')")
    @Operation(summary = "Get user details by ID")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }

    @GetMapping
    @PreAuthorize("hasRole('STORE_MANAGER')")
    @Operation(summary = "List all registered users")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok(users));
    }
}
