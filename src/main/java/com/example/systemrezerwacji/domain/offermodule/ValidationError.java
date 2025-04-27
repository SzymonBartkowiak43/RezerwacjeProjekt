package com.example.systemrezerwacji.domain.offermodule;

enum ValidationError {

    NAME_IS_EMPTY("Name cannot be empty"),
    NAME_TOO_LONG("Name exceeds the maximum allowed length"),

    DESCRIPTION_TOO_LONG("Description exceeds the maximum allowed length"),

    PRICE_IS_INVALID("Price must be greater than 0"),
    DURATION_IS_INVALID("Duration must be greater than 0");

    private final String message;

    ValidationError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
