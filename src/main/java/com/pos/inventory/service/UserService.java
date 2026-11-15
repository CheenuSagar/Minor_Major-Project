package com.pos.inventory.service;

import com.pos.inventory.dto.auth.UserDto;
import com.pos.inventory.entity.RoleType;
import com.pos.inventory.entity.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto updateStatus(Long id, boolean enabled);
    UserDto assignRoles(Long id, Set<RoleType> roles);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    User getCurrentAuthenticatedUser();
}
