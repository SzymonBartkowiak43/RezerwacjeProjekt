package com.example.systemrezerwacji.domain.usermodule;

enum ValidationError {

    EMPTY_NAME("Name shouldn't be empty"),
    EMPTY_EMAIL("Email shouldn't be empty"),
    EMPTY_PASSWORD("Password shouldn't be empty"),
    SHORT_NAME("Name is too short"),
    FORBIDDEN_CHARACTERS_IN_NAME("Name contains forbidden characters"),
    INVALID_EMAIL("Email format is invalid"),
    SHORT_PASSWORD("Password is too short"),
    NO_SPECIAL_CHARACTERS_IN_PASSWORD("Password must contain at least one special character");

    private final String message;

    ValidationError(String message) {
        this.message = message;
    }
    String getMessage() {
        return message;
    }
}
