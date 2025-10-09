package com.scb.ragdemo.service;

/**
 * Poe API Client Service Interface
 * Responsible for interacting with the Poe API
 */
public interface PoeClientService {
    
    /**
     * Send a simple message to Poe API and get a response
     * 
     * @param message User input message
     * @return AI response content
     */
    String sendMessage(String message);
    
    /**
     * Send a message with context to Poe API
     * Used for RAG mode, where context is retrieved document content
     * Combined with user question to generate more accurate answers
     * 
     * @param question User question
     * @param context Retrieved context
     * @return AI response content
     */
    String sendMessageWithContext(String question, String context);
    
    /**
     * Test Poe API connectivity
     * 
     * @return Connection status boolean
     */
    boolean testConnection();
    
    /**
     * Get Poe API configuration information
     * 
     * @return Configuration information Map
     */
    java.util.Map<String, Object> getApiInfo();
}
