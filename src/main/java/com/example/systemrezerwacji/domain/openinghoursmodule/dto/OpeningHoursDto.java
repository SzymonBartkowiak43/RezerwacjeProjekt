package com.example.systemrezerwacji.domain.openinghoursmodule.dto;


import java.time.LocalTime;

public record OpeningHoursDto(Long salonId,String dayOfWeek, LocalTime openingTime, LocalTime closingTime) {
}
