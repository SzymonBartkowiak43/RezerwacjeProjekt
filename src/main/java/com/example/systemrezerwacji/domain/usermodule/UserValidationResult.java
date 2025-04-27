package com.example.systemrezerwacji.domain.usermodule;

record UserValidationResult(String validationMessage) {
    static final String SUCCESS_MESSAGE = "success";

    static UserValidationResult success() {
        return new UserValidationResult(SUCCESS_MESSAGE);
    }

    static UserValidationResult failure(String message) {
        return new UserValidationResult(message);
    }
}
