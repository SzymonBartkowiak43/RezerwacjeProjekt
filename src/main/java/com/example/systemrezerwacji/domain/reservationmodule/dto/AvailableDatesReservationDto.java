package com.example.systemrezerwacji.domain.reservationmodule.dto;

import java.time.LocalDate;

public record AvailableDatesReservationDto(LocalDate date, Long employeeId, Long offerId) {
}
