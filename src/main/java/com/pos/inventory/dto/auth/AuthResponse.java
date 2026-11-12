package com.pos.inventory.dto.auth;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long expiresInMs;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private Set<String> roles;
}
