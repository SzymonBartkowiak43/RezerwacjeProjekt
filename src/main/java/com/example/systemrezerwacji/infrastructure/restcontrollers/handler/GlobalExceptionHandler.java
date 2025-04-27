package com.example.systemrezerwacji.infrastructure.restcontrollers.handler;

import com.example.systemrezerwacji.domain.offermodule.exception.OfferNotFoundException;
import com.example.systemrezerwacji.domain.salonmodule.exception.SalonNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SalonNotFoundException.class)
    public ResponseEntity<String> handleSalonNotFoundException(SalonNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(OfferNotFoundException.class)
    public ResponseEntity<String> handleOfferNotFoundException(OfferNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }




}
