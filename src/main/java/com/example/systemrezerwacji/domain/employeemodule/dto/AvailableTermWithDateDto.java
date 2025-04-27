package com.example.systemrezerwacji.domain.employeemodule.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AvailableTermWithDateDto(LocalTime startServices, LocalTime endServices, LocalDate date) {
}
