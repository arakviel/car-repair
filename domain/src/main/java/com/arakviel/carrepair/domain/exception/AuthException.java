package com.arakviel.carrepair.domain.exception;

public class AuthException extends RuntimeException {

    public AuthException() {
        super();
    }

    public AuthException(String message) {
        super(message);
    }
}
