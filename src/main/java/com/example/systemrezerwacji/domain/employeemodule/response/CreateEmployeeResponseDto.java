package com.example.systemrezerwacji.domain.employeemodule.response;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record CreateEmployeeResponseDto(String message, @Nullable String employeeEmail, @Nullable String employeePassword) {
}
