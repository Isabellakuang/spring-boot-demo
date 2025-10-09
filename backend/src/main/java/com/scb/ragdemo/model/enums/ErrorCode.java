package com.scb.ragdemo.model.enums;

import lombok.Getter;

/**
 * Error Code Enum
 */
@Getter
public enum ErrorCode {
    // System errors (1000-1999)
    SUCCESS(1000, "Operation successful"),
    INTERNAL_ERROR(1001, "Internal server error"),
    INVALID_PARAMETER(1002, "Invalid parameter"),
    
    // Document-related errors (2000-2999)
    DOCUMENT_NOT_FOUND(2001, "Document not found"),
    INVALID_FILE_TYPE(2002, "Unsupported file type"),
    FILE_TOO_LARGE(2003, "File size exceeds limit"),
    FILE_UPLOAD_FAILED(2004, "File upload failed"),
    DOCUMENT_PROCESSING_FAILED(2005, "Document processing failed"),
    
    // Query-related errors (3000-3999)
    QUERY_FAILED(3001, "Query failed"),
    NO_DOCUMENTS_AVAILABLE(3002, "No documents available for querying"),
    
    // API call errors (4000-4999)
    POE_API_ERROR(4001, "Poe API call failed"),
    API_TIMEOUT(4002, "API call timeout"),
    API_RATE_LIMIT(4003, "API call rate limit exceeded");
    
    private final int code;
    private final String message;
    
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
