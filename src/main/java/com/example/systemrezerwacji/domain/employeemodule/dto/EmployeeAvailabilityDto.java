package com.example.systemrezerwacji.domain.employeemodule.dto;


import java.time.LocalTime;

public record EmployeeAvailabilityDto(String dayOfWeek, LocalTime startTime, LocalTime endTime) {}