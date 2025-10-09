package com.scb.ragdemo.service;

import com.scb.ragdemo.model.dto.request.QueryRequest;
import com.scb.ragdemo.model.dto.response.QueryResponse;
import com.scb.ragdemo.model.entity.QueryHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Query Service Interface
 * Responsible for handling user query requests and intelligent routing
 */
public interface QueryService {
    
    /**
     * Process user query request
     * Automatically selects NLP or RAG mode based on the request
     * 
     * @param request Query request object
     * @return Query response result
     */
    QueryResponse processQuery(QueryRequest request);
    
    /**
     * Execute query using NLP mode
     * Directly calls Poe API
     * 
     * @param question User question
     * @return Query response result
     */
    QueryResponse queryWithNlp(String question);
    
    /**
     * Execute query using RAG mode
     * Retrieves relevant document chunks and combines them with the question to call Poe API
     * 
     * @param question User question
     * @param topK Number of top relevant document chunks to retrieve
     * @return Query response result
     */
    QueryResponse queryWithRag(String question, int topK);
    
    /**
     * Get query history with pagination support
     * 
     * @param pageable Pagination parameters
     * @return Query history paginated result
     */
    Page<QueryHistory> getQueryHistory(Pageable pageable);
    
    /**
     * Get query history filtered by mode with pagination
     * 
     * @param mode Query mode
     * @param pageable Pagination parameters
     * @return Query history paginated result
     */
    Page<QueryHistory> getQueryHistoryByMode(String mode, Pageable pageable);
    
    /**
     * Delete specified query history record
     * 
     * @param id Query history record ID
     */
    void deleteQueryHistory(Long id);
    
    /**
     * Clear all query history
     */
    void clearAllHistory();
    
    /**
     * Get query statistics information
     * 
     * @return Statistics information Map
     */
    java.util.Map<String, Object> getQueryStatistics();
    
    /**
     * Get cached query result
     * 
     * @param question User question
     * @param mode Query mode
     * @return Cached result if exists, otherwise null
     */
    QueryResponse getCachedResult(String question, String mode);
    
    /**
     * Cache query result
     * 
     * @param question User question
     * @param mode Query mode
     * @param response Query response result
     */
    void cacheResult(String question, String mode, QueryResponse response);
}
