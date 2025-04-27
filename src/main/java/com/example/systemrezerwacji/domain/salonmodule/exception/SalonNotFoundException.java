package com.example.systemrezerwacji.domain.salonmodule.exception;

public class SalonNotFoundException extends RuntimeException {
    public SalonNotFoundException(String message) {
        super(message);
    }
}
