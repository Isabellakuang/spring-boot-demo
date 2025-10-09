package com.scb.ragdemo.exception.custom;

/**
 * Document Not Found Exception
 * Thrown when a requested document does not exist
 */
public class DocumentNotFoundException extends RuntimeException {
    
    public DocumentNotFoundException(String message) {
        super(message);
    }
    
    public DocumentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DocumentNotFoundException(Long documentId) {
        super("Document not found, ID: " + documentId);
    }
}
