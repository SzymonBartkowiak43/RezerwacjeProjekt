package com.example.systemrezerwacji.domain.employeemodule.dto;

import java.time.LocalTime;

public record AvailableTermDto(LocalTime startServices, LocalTime endServices ) {
}
