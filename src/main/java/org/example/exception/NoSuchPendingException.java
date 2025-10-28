package org.example.exception;

import java.util.NoSuchElementException;

public class NoSuchPendingException extends NoSuchElementException {
    public NoSuchPendingException(String message) {
        super(message);
    }

    public NoSuchPendingException() {
        super();
    }
}
