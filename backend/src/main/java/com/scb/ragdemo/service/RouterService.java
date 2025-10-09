package com.scb.ragdemo.service;

import com.scb.ragdemo.model.enums.QueryMode;

/**
 * Intelligent Routing Service Interface
 * Responsible for automatically determining whether user questions should use NLP or RAG mode
 */
public interface RouterService {
    
    /**
     * Determine the appropriate query mode based on user question
     * 
     * @param question User question
     * @return Recommended query mode
     */
    QueryMode determineQueryMode(String question);
    
    /**
     * Check if the question contains keywords related to uploaded documents
     * 
     * @param question User question
     * @return True if document-related keywords are found
     */
    boolean containsDocumentKeywords(String question);
    
    /**
     * Get detailed routing decision information
     * 
     * @param question User question
     * @return Decision information Map including recommended mode, confidence score, matching keywords, etc.
     */
    java.util.Map<String, Object> getRoutingDecision(String question);
}
