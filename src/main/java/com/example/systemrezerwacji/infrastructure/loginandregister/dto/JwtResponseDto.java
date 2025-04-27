package com.example.systemrezerwacji.infrastructure.loginandregister.dto;

import lombok.Builder;

@Builder
public record JwtResponseDto(
        String email,
        String token
) {
}
