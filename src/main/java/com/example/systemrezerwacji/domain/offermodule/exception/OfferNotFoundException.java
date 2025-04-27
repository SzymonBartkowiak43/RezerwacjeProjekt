package com.example.systemrezerwacji.domain.offermodule.exception;

public class OfferNotFoundException extends RuntimeException{
    public OfferNotFoundException(String message) {
        super(message);
    }
}
