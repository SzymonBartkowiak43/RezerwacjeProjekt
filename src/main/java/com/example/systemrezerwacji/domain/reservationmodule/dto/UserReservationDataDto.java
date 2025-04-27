package com.example.systemrezerwacji.domain.reservationmodule.dto;

import java.time.LocalDateTime;

public record UserReservationDataDto(Long reservationId,
                                     String salonName,
                                     String employeeName,
                                     String offerName,
                                     LocalDateTime reservationDateTime) {
}

