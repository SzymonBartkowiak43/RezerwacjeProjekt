package com.example.systemrezerwacji.domain.reservationmodule.response;

import jakarta.annotation.Nullable;

public record ReservationFacadeResponse(boolean isSuccess, @Nullable String message, @Nullable String password) {
}
