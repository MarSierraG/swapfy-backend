package com.swapfy.backend.exceptions;

public class TagInUseException extends RuntimeException {
    public TagInUseException(String message) {
        super(message);
    }
}
