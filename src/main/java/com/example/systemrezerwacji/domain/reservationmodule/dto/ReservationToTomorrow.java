package com.example.systemrezerwacji.domain.reservationmodule.dto;

import java.time.LocalDateTime;

public record ReservationToTomorrow(Long salonId, Long userId, Long offerId, LocalDateTime reservationDataTime) {
}
