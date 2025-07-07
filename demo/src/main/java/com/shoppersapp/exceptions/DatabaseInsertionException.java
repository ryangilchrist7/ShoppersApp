package com.shoppersapp.exceptions;

public class DatabaseInsertionException extends RuntimeException {
    @Deprecated
    public DatabaseInsertionException(String message) {
        super(message);
    }

    @Deprecated
    public DatabaseInsertionException(String message, Throwable cause) {
        super(message, cause);
    }
}
