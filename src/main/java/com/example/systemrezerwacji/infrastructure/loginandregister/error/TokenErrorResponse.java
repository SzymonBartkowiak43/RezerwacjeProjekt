package com.example.systemrezerwacji.infrastructure.loginandregister.error;

import org.springframework.http.HttpStatus;

public record TokenErrorResponse(String message, HttpStatus status) {
}
