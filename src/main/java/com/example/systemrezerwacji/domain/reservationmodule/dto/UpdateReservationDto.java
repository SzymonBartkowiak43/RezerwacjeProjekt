package com.example.systemrezerwacji.domain.reservationmodule.dto;

import java.time.LocalDateTime;

public record UpdateReservationDto(Long reservationId, LocalDateTime newReservationDate) {
}
