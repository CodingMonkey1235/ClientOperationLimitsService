package org.example.exception;

public class NoSuchClientLimitException extends RuntimeException {
    public NoSuchClientLimitException(String message) {
        super(message);
    }

    public NoSuchClientLimitException() {
        super();
    }
}
