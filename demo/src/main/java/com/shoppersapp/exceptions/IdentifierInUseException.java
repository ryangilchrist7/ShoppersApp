package com.shoppersapp.exceptions;

public class IdentifierInUseException extends RuntimeException {
    public IdentifierInUseException(String message) {
        super(message);
    }
}