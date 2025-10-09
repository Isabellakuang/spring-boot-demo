package com.scb.ragdemo.model.enums;

/**
 * Document Processing Status Enum
 */
public enum DocumentStatus {
    /**
     * Waiting to be processed
     */
    PENDING,
    
    /**
     * Currently processing
     */
    PROCESSING,
    
    /**
     * Processing completed
     */
    COMPLETED,
    
    /**
     * Processing failed
     */
    FAILED
}
