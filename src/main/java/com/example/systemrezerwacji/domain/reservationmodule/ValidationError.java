package com.example.systemrezerwacji.domain.reservationmodule;

enum ValidationError {

    EMPTY_USER_ID("User id cannot be empty"),
    EMPTY_OFFER_ID("Offer id cannot be empty"),
    EMPTY_SALON_ID("Salon id cannot be empty"),
    EMPTY_EMPLOYEE_ID("Employee id cannot be empty"),
    EMPTY_EMAIL("Email shouldn't be empty"),
    INVALID_EMAIL("Email format is invalid"),
    EMPLOYEE_IS_BUSY("Employee is busy during the selected time."),
    PAST_DATE("Date cannot be in the past");

    final String message;

    ValidationError(String message) {
        this.message = message;
    }

    String getMessage() {
        return message;
    }
}
