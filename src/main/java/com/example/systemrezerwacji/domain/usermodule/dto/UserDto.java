package com.example.systemrezerwacji.domain.usermodule.dto;

import lombok.Builder;

import java.util.Set;


@Builder
public record UserDto(Long userId, String email, String name, String password, Set<UserRoleDto> roles) {
}
