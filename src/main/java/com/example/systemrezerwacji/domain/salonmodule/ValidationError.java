package com.example.systemrezerwacji.domain.salonmodule;

enum ValidationError {

    EMPTY_NAME("Salon name shouldn't be empty"),
    SHORT_NAME("Salon name should be longer"),

    EMPTY_CATEGORY("Salon name shouldn't be empty"),
    SHORT_CATEGORY("Category should be longer"),

    EMPTY_CITY("Salon name shouldn't be empty"),
    FORBIDDEN_CHARACTERS_IN_CITY("City contains forbidden characters"),

    EMPTY_ZIP_CODE("Salon name shouldn't be empty"),
    INCORRECT_ZIP_CODE("Zip code is invalid"),

    EMPTY_STREET("Salon name shouldn't be empty"),
    FORBIDDEN_CHARACTERS_IN_STREET("City contains forbidden characters"),

    EMPTY_NUMBER("Salon name shouldn't be empty"),
    INVALID_NUMBER("Number is invalid");


    final String message;

    ValidationError(String message) {
        this.message = message;
    }
}
