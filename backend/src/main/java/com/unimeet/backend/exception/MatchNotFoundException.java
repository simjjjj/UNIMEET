package com.unimeet.backend.exception;

public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(String message) {
        super(message);
    }
    
    public MatchNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}