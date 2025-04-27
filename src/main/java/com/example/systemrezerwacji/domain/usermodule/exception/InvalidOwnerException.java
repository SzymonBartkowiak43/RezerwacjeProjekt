package com.example.systemrezerwacji.domain.usermodule.exception;

public class InvalidOwnerException extends RuntimeException{
    public InvalidOwnerException(String message) {
        super(message);
    }
}
