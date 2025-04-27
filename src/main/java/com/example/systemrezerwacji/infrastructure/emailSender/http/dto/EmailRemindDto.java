package com.example.systemrezerwacji.infrastructure.emailSender.http.dto;

import java.time.LocalDateTime;

public record EmailRemindDto(String to,
                             String reservationName,
                             String salonName,
                             String street,
                             String number,
                             String city,
                             LocalDateTime reservationDataTime) {
}
