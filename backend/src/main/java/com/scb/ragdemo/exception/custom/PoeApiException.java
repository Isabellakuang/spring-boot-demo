package com.scb.ragdemo.exception.custom;

/**
 * Poe API Exception
 * Thrown when Poe API calls fail
 */
public class PoeApiException extends RuntimeException {
    
    private final int statusCode;
    
    public PoeApiException(String message) {
        super(message);
        this.statusCode = 500;
    }
    
    public PoeApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 500;
    }
    
    public PoeApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public PoeApiException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}
