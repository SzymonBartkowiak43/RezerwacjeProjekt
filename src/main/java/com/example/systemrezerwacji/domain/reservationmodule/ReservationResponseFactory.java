package com.example.systemrezerwacji.domain.reservationmodule;

import com.example.systemrezerwacji.domain.reservationmodule.response.ReservationFacadeResponse;
import org.springframework.stereotype.Component;

@Component
public class ReservationResponseFactory {

    ReservationFacadeResponse createSuccess(String message, String password) {
        return new ReservationFacadeResponse(true, message, password);
    }

    ReservationFacadeResponse createError(String message) {
        return new ReservationFacadeResponse(false, message, null);
    }

    ReservationFacadeResponse createSimpleResponse(boolean success) {
        return new ReservationFacadeResponse(success, success ? "success" : "failure", null);
    }
}