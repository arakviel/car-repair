package com.arakviel.carrepair.domain.exception;

public class DomainAddException extends RuntimeException {

    public DomainAddException() {
        super();
    }

    public DomainAddException(String message) {
        super(message);
    }
}
