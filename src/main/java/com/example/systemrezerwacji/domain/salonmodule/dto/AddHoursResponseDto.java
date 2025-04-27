package com.example.systemrezerwacji.domain.salonmodule.dto;

import com.example.systemrezerwacji.domain.openinghoursmodule.OpeningHours;
import jakarta.annotation.Nullable;

import java.util.List;

public record AddHoursResponseDto(String message, @Nullable List<OpeningHours> openingHours) {
}
