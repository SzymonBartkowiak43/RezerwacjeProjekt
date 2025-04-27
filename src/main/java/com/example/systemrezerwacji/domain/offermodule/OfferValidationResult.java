package com.example.systemrezerwacji.domain.offermodule;

record OfferValidationResult(String validationMessage) {
    static final String SUCCESS_MESSAGE = "success";

    static OfferValidationResult success() {
        return new OfferValidationResult(SUCCESS_MESSAGE);
    }

    static OfferValidationResult failure(String message) {
        return new OfferValidationResult(message);
    }
}
