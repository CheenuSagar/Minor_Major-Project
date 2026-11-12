package com.pos.inventory.dto.auth;

import com.pos.inventory.entity.RoleType;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignRoleRequest {

    @NotEmpty(message = "At least one role must be provided")
    private Set<RoleType> roles;
}
