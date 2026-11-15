package com.pos.inventory.service;

import com.pos.inventory.dto.auth.AuthResponse;
import com.pos.inventory.dto.auth.LoginRequest;
import com.pos.inventory.dto.auth.RegisterRequest;
import com.pos.inventory.dto.auth.UserDto;

public interface AuthService {
    UserDto register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void logout(String bearerToken);
}
