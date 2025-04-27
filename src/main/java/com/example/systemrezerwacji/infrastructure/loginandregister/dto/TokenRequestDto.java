package com.example.systemrezerwacji.infrastructure.loginandregister.dto;


public record TokenRequestDto(
        String email,
        String password
) {
}
