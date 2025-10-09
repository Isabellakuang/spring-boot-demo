package com.scb.ragdemo.model.enums;

/**
 * Query Mode Enum
 */
public enum QueryMode {
    /**
     * NLP mode - Direct LLM response
     */
    NLP,
    
    /**
     * RAG mode - Retrieval-Augmented Generation with document context
     */
    RAG,
    
    /**
     * Auto mode - Automatically choose between NLP and RAG
     */
    AUTO
}
