package com.example.systemrezerwacji.domain.salonmodule.dto;

import jakarta.annotation.Nullable;

public record SalonFacadeResponseDto(String message, @Nullable Long salonId) {
}
