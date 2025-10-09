package com.scb.ragdemo.exception.custom;

/**
 * Invalid File Exception
 * Thrown when an uploaded file is invalid (wrong type, too large, corrupted, etc.)
 */
public class InvalidFileException extends RuntimeException {
    
    public InvalidFileException(String message) {
        super(message);
    }
    
    public InvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
